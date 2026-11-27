package org.jedi_bachelor.ioboxstarter.constants;

public final class MessageHeaderConstants {

  private MessageHeaderConstants() {}

  public static final String MESSAGE_ID = "messageId";

  public static final String TIMESTAMP = "timestamp";

  public static final String GROUP_ID = "groupId";

  public static final String JMS_GROUP_ID = "JMSXGroupID";

  public static final String ERROR_MESSAGE = "errorMessage";

  public static final String QUEUE_NAME = "queueName";

  public static final String PAYLOAD = "payload";

  // Default values in properties

  public final static class Default {

    private Default() {}

    public static final String DEFAULT_GROUP_ID = "iobox-group";
  }
}
