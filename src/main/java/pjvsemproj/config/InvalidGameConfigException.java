package pjvsemproj.config;

/**
 * Thrown by validation pipelines when a file definition or live matrix fails domain configuration rules.
 */
public class InvalidGameConfigException extends RuntimeException {

    /**
     * Constructs a new InvalidGameConfigException with a targeted diagnostic details message.
     *
     * @param message a description detailing the precise structural or contextual validation failure
     */
    public InvalidGameConfigException(String message) {
        super(message);
    }
}