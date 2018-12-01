package org.jaun.clubmanager.member.application.resource;

import java.util.ArrayList;
import java.util.List;

public class MemberCsvImportResultDTO extends DTO {

    private List<String> imported = new ArrayList<>();
    private List<String> ignored = new ArrayList<>();
    private List<FailedImport> failed = new ArrayList<>();

    public static class FailedImport extends DTO {
        private final String subscriptionId;
        private final String error;

        FailedImport(String subscriptionId, String error) {
            this.subscriptionId = subscriptionId;
            this.error = error;
        }

        public String getSubscriptionId() {
            return subscriptionId;
        }

        public String getError() {
            return error;
        }
    }

    public int getTotalImported() {
        return imported.size();
    }

    public int getTotalFailed() {
        return failed.size();
    }

    public int getTotalIgnored() {
        return ignored.size();
    }

    public List<String> getImportedIds() {
        return imported;
    }

    public List<FailedImport> getFailed() {
        return failed;
    }

    public List<String> getIgnoredIds() {
        return ignored;
    }
}
