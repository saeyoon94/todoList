package practice.todoList.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Plan implements Cloneable{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userId;
    private LocalDate date;
    private String plan;
    private Boolean finish;

    //계획 일괄등록할 때 객체 깊은복사를 위한 메소드
    @Override
    public Plan clone() throws CloneNotSupportedException {
        return (Plan)super.clone();
    }
}
