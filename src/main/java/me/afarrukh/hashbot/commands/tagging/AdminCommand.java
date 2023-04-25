package me.afarrukh.hashbot.commands.tagging;

/**
 * A tagging interface. The CommandManager object uses this before executing the command
 * to ensure that the Member object, as part of the event, that wrote the message that invoked this command
 * has sufficient rights to execute this.
 * <p>
 * Any command that aims to control the server administration can use this.
 *
 * @see me.afarrukh.hashbot.core.CommandManager
 */
public interface AdminCommand {}
