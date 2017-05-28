package de.syscy.kagecloud.plugin;

import com.google.common.base.Preconditions;

import de.syscy.kagecloud.CommandSender;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * A command that can be executed by a {@link CommandSender}.
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.NONE)
public abstract class Command {
	private final String name;
	private final boolean requiresOp;

	/**
	 * Construct a new command with no permissions or aliases.
	 *
	 * @param name the name of this command
	 */
	public Command(String name) {
		this(name, false);
	}

	/**
	 * Construct a new command.
	 *
	 * @param name primary name of this command
	 * @param permission the permission node required to execute this command,
	 * null or empty string allows it to be executed by everyone
	 * @param aliases aliases which map back to this command
	 */
	public Command(String name, boolean requiresOp) {
		Preconditions.checkArgument(name != null, "name");

		this.name = name;
		this.requiresOp = requiresOp;
	}

	/**
     * Execute this command with the specified sender and arguments.
     *
     * @param sender the executor of this command
     * @param args arguments used to invoke this command
     */
    public abstract void execute(CommandSender sender, String[] args);
}
