package de.syscy.kagecloud;

import de.syscy.kagecloud.chat.BaseComponent;

public interface CommandSender {
	/**
     * Get the unique name of this command sender.
     *
     * @return the senders username
     */
    public String getName();

	/**
     * Send a message to this sender.
     *
     * @param message the message to send
     */
    public void sendMessage(String message);

	/**
     * Send several messages to this sender. Each message will be sent
     * separately.
     *
     * @param messages the messages to send
     */
    public void sendMessages(String... messages);

	/**
     * Send a message to this sender.
     *
     * @param message the message to send
     */
    public void sendMessage(BaseComponent... message);

	/**
     * Send a message to this sender.
     *
     * @param message the message to send
     */
    public void sendMessage(BaseComponent message);

	/**
     * Checks if this player is a server operator
     *
     * @return whether they are a server operator
     */
    public boolean isOp();
}