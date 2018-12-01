package org.jaun.clubmanager.person.application.resource;

import java.util.ArrayList;
import java.util.List;

public class PersonCsvImportResultDTO extends DTO {

    private List<String> importedIds = new ArrayList<>();
    private List<String> ignoredIds = new ArrayList<>();
    private List<FailedImport> failed = new ArrayList<>();

    public static class FailedImport extends DTO {
        private final String id;
        private final String error;

        FailedImport(String id, String error) {
            this.id = id;
            this.error = error;
        }

        public String getId() {
            return id;
        }

        public String getError() {
            return error;
        }
    }

    public int getTotalImported() {
        return importedIds.size();
    }

    public int getTotalFailed() {
        return failed.size();
    }

    public int getTotalIgnored() {
        return ignoredIds.size();
    }

    public List<String> getImportedIds() {
        return importedIds;
    }

    public List<FailedImport> getFailed() {
        return failed;
    }

    public List<String> getIgnoredIds() {
        return ignoredIds;
    }
}
