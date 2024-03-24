package me.wonka01.ServerQuests.events;

import me.wonka01.ServerQuests.enums.ObjectiveType;
import me.wonka01.ServerQuests.questcomponents.ActiveQuests;
import me.wonka01.ServerQuests.questcomponents.QuestController;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.List;

public class CraftItemQuestEvent extends QuestListener implements Listener {

    public CraftItemQuestEvent(ActiveQuests activeQuests) {
        super(activeQuests);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();

        int amount = event.getRecipe().getResult().getAmount();

        List<QuestController> controllers = tryGetControllersOfObjectiveType(ObjectiveType.CRAFT_ITEM);
        for (QuestController controller : controllers) {
            updateQuest(controller, player, amount, ObjectiveType.CRAFT_ITEM, event.getRecipe().getResult().getType());
        }
    }
}
