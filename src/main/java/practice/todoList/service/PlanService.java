package practice.todoList.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.todoList.domain.Plan;
import practice.todoList.domain.WeekDay;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface PlanService {
    public Map<LocalDate, List<Map<String, List<?>>>> findPlanAndMemoOfWeek(String userId, LocalDate currentDate);

    //public void registerPlan(Map<String, ?> newPlan);
    public void registerPlan(String requestUserId, Plan plan);

    public Plan modifyPlan(String requestUserId, Plan plan);

    public void deletePlan(String requestUserId, int id);

    public List<Plan> registerPlans(String requestUserId, Plan plan, WeekDay weekDay) throws CloneNotSupportedException;

}
