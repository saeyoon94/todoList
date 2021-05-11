package practice.todoList.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.todoList.domain.Plan;
import practice.todoList.domain.WeekDay;
import practice.todoList.repository.NoteRepository;
import practice.todoList.repository.PlanRepository;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService{
    private final PlanRepository planRepository;
    private final NoteRepository noteRepository;

    @Override
    public Map<LocalDate, List<Map<String, List<?>>>> findPlanAndMemoOfWeek(String userId, LocalDate currentDate) { //Date는 월요일

        Map<LocalDate, List<Map<String, List<?>>>> result = new HashMap<>();
        int gapFromMonday = currentDate.getDayOfWeek().getValue() - 1; //DayOfWeek -> 요일. 1:월 ~ 7:일
        LocalDate monday = currentDate.minusDays(gapFromMonday);  //월요일 날짜

        for (int i = 0; i <= 6; i++) {
            ArrayList<Map<String, List<?>>> plansAndNotesForOneDay = new ArrayList<Map<String, List<?>>>();
            HashMap<String, List<?>> plansOrNotes = new HashMap<>();

            plansOrNotes.put("plan", planRepository.findByUserIdAndDate(userId, monday.plusDays(i)));
            plansOrNotes.put("note", noteRepository.findByUserIdAndDate(userId, monday.plusDays(i)));

            plansAndNotesForOneDay.add(plansOrNotes);
            result.put(monday.plusDays(i), plansAndNotesForOneDay);
        }

        return result;
    }

    @Override
    public void registerPlan(Plan plan) {
        planRepository.save(plan);
    }

    @Override
    public Plan modifyPlan(String requestUserId, Plan plan) throws IllegalStateException{
        int id = plan.getId();
        String newPlan = plan.getPlan();
        Boolean newFinish = plan.getFinish();

        Plan asisPlan = planRepository.findById(id).get();
        if (asisPlan.getUserId() != requestUserId) {
            throw new IllegalStateException("Unauthorized Request");
        }
        asisPlan.setPlan(newPlan);
        asisPlan.setFinish(newFinish);
        return asisPlan;
    }

    @Override
    public void deletePlan(String requestUserId, int id) {
        if (planRepository.findById(id).get().getUserId() != requestUserId) {
            throw new IllegalStateException("Unauthorized Request");
        }
        planRepository.deleteById(id);
    }

    @Override
    public List<Plan> registerPlans(Plan plan, WeekDay weekDay) throws CloneNotSupportedException {
        LocalDate startDate = plan.getDate(); //월요일
        planRepository.save(plan);

        ArrayList<Plan> plans = new ArrayList<>();
        plans.add(plan);

        int weekendDays = 0;
        if (weekDay == WeekDay.WEEK_DAY) {
            weekendDays = 2;
        }

        for (int i = 1; i <= 6 - weekendDays; i++) {
            Plan samePlan = plan.clone();
            samePlan.setId(null);   //id가 바뀌지 않아서 자동생성되도록 null로 지정. 기존 아이디에서 i만큼 더하게 하면 동시성 문제가 있을듯
            samePlan.setDate(startDate.plusDays(i));
            planRepository.save(samePlan);
            plans.add(samePlan);
        }

        return plans;
    }

}
