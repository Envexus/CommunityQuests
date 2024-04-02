package me.wonka01.ServerQuests.questcomponents;

import lombok.Getter;
import lombok.NonNull;
import me.wonka01.ServerQuests.enums.EventType;
import me.wonka01.ServerQuests.enums.ObjectiveType;
import me.wonka01.ServerQuests.objectives.Objective;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;

@Getter
public class QuestData {

    private final @NonNull String questId;
    private final String questType;
    private final String displayName;
    private final String description;
    private final Material displayItem;
    private final @NonNull EventType eventType = EventType.COLLAB;
    private int questDuration;
    private String afterQuestCommand;
    private String beforeQuestCommand;
    private List<Objective> objectives;

    public QuestData(String displayName, String description, String questType,
            int questDuration, Material displayItem, @NonNull String questId, String afterQuestCommand,
            String beforeQuestCommand, List<Objective> objectives) {
        this.questDuration = questDuration;
        this.displayName = displayName;
        this.description = description == null ? "" : description;
        this.questId = questId;
        this.questType = questType;
        this.displayItem = displayItem;
        this.afterQuestCommand = afterQuestCommand;
        this.beforeQuestCommand = beforeQuestCommand;
        this.objectives = objectives;
    }

    public double getAmountCompleted() {
        return objectives.stream().mapToDouble(Objective::getAmountComplete).sum();
    }

    public double getQuestGoal() {
        return objectives.stream().mapToDouble(Objective::getGoal).sum();
    }

    public double getAmountCompletedByType(ObjectiveType type) {
        return objectives.stream().filter(objective -> objective.getType() == type)
                .mapToDouble(Objective::getAmountComplete)
                .sum();
    }

    public double getQuestGoalByType(ObjectiveType type) {
        double result = objectives.stream().filter(objective -> objective.getType() == type)
                .mapToDouble(Objective::getGoal)
                .sum();
        Bukkit.getServer().broadcastMessage("Getting goal by type " + result);
        return result;
    }

    public double getPercentageComplete() {
        return getAmountCompleted() / getQuestGoal();
    }

    public void addToQuestProgress(double amountToIncrease, Objective objective) {
        objective.addToObjectiveProgress(amountToIncrease);
    }

    public List<ObjectiveType> getObjectiveTypes() {
        return objectives.stream().map(Objective::getType).collect(Collectors.toList());
    }

    public void decreaseDuration(int amountToDecrease) {
        questDuration -= amountToDecrease;
    }

    public boolean isGoalComplete(Objective objective) {
        return objective.isGoalComplete();
    }

    // Always false if no goal is set and the quest is using a timer...
    public boolean isGoalComplete() {
        return (hasGoal() && getAmountCompleted() >= getQuestGoal());
    }

    public boolean hasGoal() {
        return getQuestGoal() > 0;
    }
}
