package me.afarrukh.hashbot.commands.tagging;

/**
 * Any command that involves the user credit. An EconCommand is usually one where the user plays some sort of
 * 'game', usually a game of chance, such as flipping a coin, in which they either lose or gain credits depending on
 * the outcome.
 *
 * @see me.afarrukh.hashbot.commands.econ.FlipCommand
 */
public interface EconCommand extends CategorisedCommand {}
