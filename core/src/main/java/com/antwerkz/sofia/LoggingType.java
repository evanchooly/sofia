package com.antwerkz.sofia;

public enum LoggingType {
    NONE {
        @Override
        public String getImports() {
            return "";
        }

        @Override
        public String[] getLoggingLevels() {
            return new String[0];
        }
    },

    SLF4J {
        @Override
        public String[] getLoggingLevels() {
            return new String[] {"error", "debug", "warn", "info"};
        }

        @Override
        public String getImports() {
            return "import org.slf4j.*;";
        }
    };

    public abstract String[] getLoggingLevels();

    public abstract String getImports();
}
