package exceptions;

public class Exceptions {

    public static class JConfFailedToCreateConfigException extends Exception {
        public JConfFailedToCreateConfigException(final String message) {
            super(message);
        }

        public JConfFailedToCreateConfigException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    public static class JConfCouldNotReadFileConfigException extends JConfFailedToCreateConfigException {
        public JConfCouldNotReadFileConfigException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    public static class JConfFailedToMapJsonToObjectConfig extends JConfFailedToCreateConfigException {
        public JConfFailedToMapJsonToObjectConfig(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    public static class JConfPathToJsonFileDoesNotExistConfigException extends JConfFailedToCreateConfigException {
        public JConfPathToJsonFileDoesNotExistConfigException(final String s) {
            super(s);
        }
    }

    public static class JConfFailedToWriteConfigException extends Exception {
        public JConfFailedToWriteConfigException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

}
