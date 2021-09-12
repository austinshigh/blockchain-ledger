package com.cscie97.ledger;

public class CommandProcessorException extends Exception{

    private String command;
    private String reason;
    private int lineNumber;

    public CommandProcessorException(Exception e, String reason) {
        super(reason);
        this.reason = reason;
    }

    public CommandProcessorException(CommandProcessorException e) {
        super();
        this.command = e.getCommand();
        this.reason = e.getReason();
    }

//    public CommandProcessorException(Exception e, String command, String reason) {
//        super(reason);
//        this.command = command;
//        this.reason = reason;
//    }

    public CommandProcessorException(String reason) {
        super(reason);
        this.reason = reason;
    }

    /**
     * to string method
     * @return {@link String}
     * @see String
     */
    @Override
    public String toString() {
        return "CommandProcessorException:" +
                "\ncommand = '" + command + '\'' +
                "\nreason = '" + reason + '\'' +
                "\nlineNumber = " + lineNumber;
    }

    /**
     * get reason
     *
     * @return {@link String}
     * @see String
     */
    public String getReason() {
        return reason;
    }

    /**
     * get line number
     *
     * @return {@link int}
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * get command
     *
     * @return {@link String}
     * @see String
     */
    public String getCommand() {
        return command;
    }

    /**
     * set command
     *
     * @param command command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * set reason
     *
     * @param reason reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * set line number
     *
     * @param lineNumber lineNumber
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
