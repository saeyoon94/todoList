package practice.todoList.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import practice.todoList.domain.Plan;
import practice.todoList.service.PlanService;

public class RegisterPlanHandler extends SessionHandler{
    private final PlanService planService;

    public RegisterPlanHandler(PlanService planService) {
        this.planService = planService;
    }

    @Override
    public ResponseEntity process(String userId) {
        Plan plan = (Plan) entity;

        plan.setUserId(userId);
        planService.registerPlan(plan);
        return new ResponseEntity<>(plan, HttpStatus.OK);
    }
}
