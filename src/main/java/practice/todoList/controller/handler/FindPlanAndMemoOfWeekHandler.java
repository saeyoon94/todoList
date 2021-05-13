package practice.todoList.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import practice.todoList.service.PlanService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class FindPlanAndMemoOfWeekHandler extends SessionHandler{
    private final PlanService planService;

    public FindPlanAndMemoOfWeekHandler(PlanService planService) {
        this.planService = planService;
    }

    @Override
    public ResponseEntity process(String userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.parse((String)param, formatter);

        Map<LocalDate, List<Map<String, List<?>>>> plansAndMemosOfWeek = planService.findPlanAndMemoOfWeek(userId, currentDate);
        return new ResponseEntity<>(plansAndMemosOfWeek, HttpStatus.OK);
    }

}
