package me.wonka01.ServerQuests.questcomponents;

import lombok.Getter;
import lombok.NonNull;
import me.knighthat.apis.utils.Colorization;
import me.wonka01.ServerQuests.ServerQuests;
import me.wonka01.ServerQuests.enums.EventType;
import me.wonka01.ServerQuests.enums.ObjectiveType;
import me.wonka01.ServerQuests.objectives.Objective;
import me.wonka01.ServerQuests.questcomponents.bossbar.QuestBar;
import me.wonka01.ServerQuests.questcomponents.players.BasePlayerComponent;
import me.wonka01.ServerQuests.questcomponents.schedulers.QuestTimer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

@Getter
public class QuestController implements Colorization {

    private final QuestBar questBar;
    private final QuestData questData;
    private final BasePlayerComponent playerComponent;
    private final EventConstraints eventConstraints;
    private final UUID questId;
    private final ServerQuests plugin;

    public QuestController(ServerQuests plugin, QuestData questData, QuestBar questBar,
            BasePlayerComponent playerComponent, EventConstraints eventConstraints) {
        this.plugin = plugin;
        this.questData = questData;
        this.questBar = questBar;
        this.playerComponent = playerComponent;
        questId = UUID.randomUUID();
        this.eventConstraints = eventConstraints;

        if (questData.getQuestDuration() > 0) {
            new QuestTimer(this);
        }
    }

    public boolean updateQuest(double count, Player player, ObjectiveType type) {
        double amountToAdd = count;

        if (questData.hasGoal()) {
            if (amountToAdd > questData.getQuestGoal() - questData.getAmountCompleted()) {
                amountToAdd = questData.getQuestGoal() - questData.getAmountCompleted();
            }
            questData.addToQuestProgress(amountToAdd, type);
        }

        playerComponent.savePlayerAction(player, amountToAdd);
        updateBossBar();
        sendPlayerMessage(player, count);

        if (getQuestData().isGoalComplete()) {
            endQuest();
        }
        return getQuestData().isGoalComplete();
    }

    public void endQuest() {
        if (questData.hasGoal() && !questData.isGoalComplete() && questData.getEventType().equals(EventType.COLLAB)) {
            broadcast("questFailureMessage");
            playerComponent.sendLeaderString();
        } else {
            broadcast("questCompleteMessage");
            playerComponent.sendLeaderString();
            playerComponent.giveOutRewards(questData.getQuestGoal());
        }

        if (questData.getAfterQuestCommand() != null || !questData.getAfterQuestCommand().isEmpty()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), questData.getAfterQuestCommand());
        }

        ActiveQuests.getActiveQuestsInstance().endQuest(questId);
    }

    public void removeBossBar() {
        questBar.removeBossBar();
    }

    public boolean isCompetitive() {
        return (questData.getEventType().equals(EventType.COMPETITIVE));
    }

    private void updateBossBar() {
        double barProgress = questData.getPercentageComplete();
        questBar.updateBarProgress(barProgress);
    }

    public List<ObjectiveType> getObjectiveTypes() {
        return questData.getObjectiveTypes();
    }

    private final DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    private void sendPlayerMessage(Player player, double count) {
        if (!player.hasPermission("communityquests.showmessages"))
            return;

        String message = color(
                plugin.messages().message("contributionMessage", questData)
                        .replaceAll("contributionCount", decimalFormat.format(count)));
        player.sendMessage(message);
    }

    public void broadcast(@NonNull String messagePath) {
        String message = color(plugin.messages().message(messagePath, questData));
        plugin.getServer().broadcastMessage(message);
    }
}
