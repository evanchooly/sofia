package com.antwerkz.sofia;

import java.util.logging.Level;

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
    },

    JUL {
        @Override
        public String getImports() {
            return "import java.util.logging.*;";
        }

        @Override
        public String[] getLoggingLevels() {
            return new String[] {
                Level.ALL.toString().toLowerCase(),
                Level.SEVERE.toString().toLowerCase(),
                Level.WARNING.toString().toLowerCase(),
                Level.INFO.toString().toLowerCase(),
                Level.CONFIG.toString().toLowerCase(),
                Level.FINE.toString().toLowerCase(),
                Level.FINER.toString().toLowerCase(),
                Level.FINEST.toString().toLowerCase()
            };
        }
    };

    public abstract String[] getLoggingLevels();

    public abstract String getImports();
}
