package team.unnamed.hephaestus.commands;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.SubCommandClasses;

@Command(names = {"hephaestus"}, permission = "hephaestus.admin")
@SubCommandClasses({
        SummonCommand.class,
        EvalCommand.class
})
public class HephaestusCommand implements CommandClass {
}