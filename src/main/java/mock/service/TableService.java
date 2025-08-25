package mock.service;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import mock.model.SetTableDTO;
import mock.model.error_response.ErrorBody;
import mock.model.error_response.ErrorResponse;
import mock.model.table_value.AllTablesResult;
import mock.model.table_value.TableResult;
import mock.model.table_value.TableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@ApplicationScoped
public class TableService {
    private static final Logger log = LoggerFactory.getLogger(TableService.class);

    @Inject
    FileReaderService readerService;

    @Inject
    ResponseService responseService;

    final String tableName = "u_test_table";
    Map<String, AllTablesResult> tableMap = new ConcurrentHashMap<>();

    private final Map<String, Function<TableValue, String>> FIELD_ACCESSORS = Map.ofEntries(
            Map.entry("u_long_field", TableValue::getuLongField),
            Map.entry("u_date_field", TableValue::getuDateField),
            Map.entry("sys_mod_count", TableValue::getSysModCount),
            Map.entry("u_float_field", TableValue::getuFloatField),
            Map.entry("u_string_field", TableValue::getuStringField),
            Map.entry("u_decimal_field", TableValue::getuDecimalField),
            Map.entry("sys_updated_on", TableValue::getSysUpdatedOn),
            Map.entry("sys_tags", TableValue::getSysTags),
            Map.entry("u_boolean_field", TableValue::getuBooleanField),
            Map.entry("sys_class_name", TableValue::getSysClassName),
            Map.entry("sys_id", TableValue::getSysId),
            Map.entry("sys_updated_by", TableValue::getSysUpdatedBy),
            Map.entry("sys_created_on", TableValue::getSysCreatedOn),
            Map.entry("u_integer_field", TableValue::getuIntegerField),
            Map.entry("u_string_utf8_field", TableValue::getuStringUtf8Field),
            Map.entry("sys_created_by", TableValue::getSysCreatedBy),
            Map.entry("u_color_field", TableValue::getuColorField),
            Map.entry("u_phone_field", TableValue::getuPhoneField),
            Map.entry("u_url_field", TableValue::getuUrlField)
    );

    public Uni<Response> getAllTableData(String table, Map<String, List<String>> queryParams, long requestTime) {
        return Uni.createFrom().item(() -> {
            Object result = getTable(table);
            log.info("Asked: {}", table);
            if (result != null) {
                if (queryParams != null) {
                    log.info("Query: {}", queryParams);
                    AllTablesResult allTablesResult = null;
                    try {
                        allTablesResult = (AllTablesResult) result;
                    } catch (ClassCastException e) {
                        log.info("Returned: {}", table);
                        return responseService.buildResponseWithHeaders(result, 200, requestTime, 5930, "");//это какая-то другая таблица
                    }
                    Map<String, String> sysParams = parseSysparmQuery(queryParams);
                    queryParams.forEach((k, v) -> log.info("{} = {}", k, v));
                    if (allTablesResult != null) {//данные есть
                        List<TableValue> values = allTablesResult.getResult();
                        if (!values.isEmpty()) {// нет элементов - нет и фильтрации
                            for (Map.Entry<String, String> entry : sysParams.entrySet()) {
                                String field = entry.getKey();
                                String value = entry.getValue();
                                values = filterValues(field, value, values);
                                if (values.isEmpty()) break;//элементов уже нет - нет смысла продолжать фильтрацию
                            }
                        }
                        return responseService.buildResponseWithHeaders(new AllTablesResult(values), 200, requestTime, values.size(), "");
                    } else return responseService.buildResponseWithHeaders(new AllTablesResult(Collections.emptyList()), 200, requestTime, 0, "");//нет данных - пусто
                } else return responseService.buildResponseWithHeaders(result, 200, requestTime, 36, "");// нет квери - нет фильтрации
            } else return getBadResponse(404, requestTime);// бананов у нас нет
        });
    }

    public Uni<Response> postDataToTable(String authHeader, String table, SetTableDTO dto, long requestTime) {
        return Uni.createFrom().item(() -> {
            AllTablesResult result = null;
            try {
                result = (AllTablesResult) getTable(table);
            } catch (ClassCastException e) {
                return getBadResponse(405, requestTime);
            }

            if (result != null) {
                List<TableValue> oldValues = result.getResult();
                String author = getSecuredData(authHeader);
                TableValue newValue = new TableValue(dto, author);

                // Обновление в фоне
                CompletableFuture.runAsync(() -> {
                    List<TableValue> list = new ArrayList<>(oldValues);
                    list.add(newValue);
                    tableMap.put(table, new AllTablesResult(list));
                });

                return responseService.buildResponseWithHeaders(new TableResult(newValue), 201, requestTime, 1, newValue.getSysId());
            } else {
                return getBadResponse(404, requestTime);
            }
        });
    }

    public Uni<Response> patchDataToTable(String authHeader, String tableName, String sysId, SetTableDTO dto, long requestTime) {
        return Uni.createFrom().item(() -> {
            AllTablesResult result = null;
            try {
                result = (AllTablesResult) getTable(tableName);
            } catch (ClassCastException e) {
                return getBadResponse(405, requestTime);
            }
            if (result != null) {
                String author = getSecuredData(authHeader);
                List<TableValue> list = new ArrayList<>(result.getResult());

                // Найти индекс объекта
                int index = -1;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getSysId().equals(sysId)) {
                        index = i;
                        break;
                    }
                }

                if (index == -1) return getBadResponse(404, requestTime); // не нашли объект

                // Обновляем найденный объект
                TableValue oldValue = list.get(index);
                TableValue updatedValue = TableValue.patched(oldValue, dto, author);
                list.set(index, updatedValue); // заменяем старый объект новым

                // Обновляем map асинхронно
                CompletableFuture.runAsync(() -> tableMap.put(tableName, new AllTablesResult(list)));

                return responseService.buildResponseWithHeaders(new TableResult(updatedValue), 200, requestTime, 1, "");
            } else {
                return getBadResponse(404, requestTime);
            }
        });
    }

    public Uni<Response> deleteDataFromTable(String tableName, String sysId, long requestTime) {
        return Uni.createFrom().item(() -> {
            AllTablesResult result = null;
            try {
                result = (AllTablesResult) getTable(tableName);
            } catch (ClassCastException e) {
                return getBadResponse(405, requestTime);
            }
            if (result != null) {
                List<TableValue> list = new ArrayList<>(result.getResult());

                boolean removed = list.removeIf(val -> val.getSysId().equals(sysId));
                if (!removed) return getBadResponse(404, requestTime);

                // Обновляем map асинхронно
                CompletableFuture.runAsync(() -> tableMap.put(tableName, new AllTablesResult(list)));

                return responseService.buildResponseWithHeaders("", 204, requestTime, 0, "");
            } else {
                return getBadResponse(404, requestTime);
            }
        });
    }

    public Uni<Response> resetTable() {
        return Uni.createFrom().item(() -> {
            tableMap.remove(tableName);
            try {
                AllTablesResult result = readerService.readFromJson(tableName, AllTablesResult.class);
                tableMap.put(tableName, result);
                log.info("Reset table: {}", tableName);
                return Response.status(200).entity(tableName + " reset successfully").build();
            } catch (IOException e) {
                log.error("Failed to reset {}: {}", tableName, e.getMessage());
                return Response.status(500).entity(e.getMessage()).build();
            }
        });
    }

//    -------------------------------------------UTIL METHODS----------------------------------------------------------

    private Response getBadResponse(int statusCode, long requestTime) {
        return switch (statusCode) {
            case 404 -> responseService.buildResponseWithHeaders(new ErrorResponse(
                    new ErrorBody("No Record found",
                            "Records matching query not found. Check query parameter or offset parameter"),
                    "failure"), statusCode, requestTime, 0, "");
            case 405 -> responseService.buildResponseWithHeaders(new ErrorResponse(
                    new ErrorBody("Method not allowed",
                            "Method not allowed for this table"),
                    "failure"), statusCode, requestTime, 0, "");
            default -> responseService.buildResponseWithHeaders(new ErrorResponse(
                    new ErrorBody("Internal Server Error",
                            "null"),
                    "failure"), statusCode, requestTime, 0, "");
        };
    }

    void onStart(@Observes StartupEvent ev) {
        init();
    }

    public void init() {
        CompletableFuture.supplyAsync(() -> {
            try {
                AllTablesResult allTablesResult = readerService.readFromJson(tableName, AllTablesResult.class);
                this.tableMap.put(tableName, allTablesResult);
                log.info("Table data cache set");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return null;
        });
    }

    private String getSecuredData(String authHeader) {
        String username = "";
        if (authHeader != null && authHeader.toLowerCase().startsWith("basic ")) {
            // Снимаем "Basic " и декодируем base64
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);

            // credentials = "username:password"
            final String[] values = credentials.split(":", 2);
            username = values[0];
        }
        return username;
    }

    public Map<String, String> parseSysparmQuery(Map<String, List<String>> queryParams) {
        Map<String, String> result = new HashMap<>();

        if (queryParams == null || queryParams.isEmpty()) return result;

        // 1. Обработка sysparm_query
        List<String> sysparmList = queryParams.get("sysparm_query");
        if (sysparmList != null && !sysparmList.isEmpty()) {
            String rawQuery = sysparmList.getFirst();
            String normalized = rawQuery.replace("^", "&");

            Arrays.stream(normalized.split("&"))
                    .map(s -> s.split("=", 2))
                    .filter(arr -> arr.length == 2)
                    .forEach(arr -> result.put(arr[0], arr[1]));
        }

        // 2. Остальные параметры (кроме sysparm_query)
        queryParams.forEach((key, valueList) -> {
            if (!key.equals("sysparm_query") && valueList != null && !valueList.isEmpty()) {
                result.put(key, valueList.getFirst()); // только первое значение
            }
        });

        return result;
    }

    private Object getTable(String table) {
        if (tableMap.containsKey(table)) {
            log.info("Table found: {}", table);
            return tableMap.get(table);
        } else {
            try {
                if (table.equals(this.tableName)) {
                    // Пытаемся прочитать как полноценную AllTablesResult таблицу
                    AllTablesResult result = readerService.readFromJson(table, AllTablesResult.class);
                    tableMap.put(table, result);
                    log.info("Lazily loaded table: {}", table);
                    return result;
                } else return readerService.formatJsonString(table);
            } catch (IOException e) {
                log.warn("Table {} is not real data table. Returning raw json string fallback", table);
                try {
                    return readerService.formatJsonString(table);
                } catch (IOException ex) {
                    log.error("Error reading raw JSON for {}: {}", table, ex.getMessage());
                    return null;
                }
            }
        }
    }

    private List<TableValue> filterValues(String field, String value, List<TableValue> values) {
        Function<TableValue, String> extractor = FIELD_ACCESSORS.get(field);

        if (extractor == null) return values; // ничего не фильтруем

        return values.stream()
                .filter(tv -> {
                    String actual = extractor.apply(tv);
                    return actual != null && actual.equals(value);
                })
                .toList();
    }
}
