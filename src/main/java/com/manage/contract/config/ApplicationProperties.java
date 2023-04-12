package com.manage.contract.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Contractmanager.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final ApplicationProperties.Storage storage = new ApplicationProperties.Storage();

    public ApplicationProperties() {}

    public Storage getStorage() {
        return storage;
    }

    public static class Storage {

        private final BinaryFile binaryFile = new BinaryFile();

        public Storage() {}

        public BinaryFile getBinaryFile() {
            return binaryFile;
        }

        public static class BinaryFile {

            private String currentVersion;
            private String archive;

            public BinaryFile() {}

            public String getCurrentVersion() {
                return currentVersion;
            }

            public void setCurrentVersion(String currentVersion) {
                this.currentVersion = currentVersion;
            }

            public String getArchive() {
                return archive;
            }

            public void setArchive(String archive) {
                this.archive = archive;
            }
        }
    }
}
