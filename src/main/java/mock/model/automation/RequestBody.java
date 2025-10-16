package mock.model.automation;

public record RequestBody (
       String userName,
       String apiToken,
       int organizationId
) {}
