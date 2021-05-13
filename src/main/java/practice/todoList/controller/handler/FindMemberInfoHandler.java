package practice.todoList.controller.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import practice.todoList.domain.Member;
import practice.todoList.service.MemberInquiryService;

public class FindMemberInfoHandler extends SessionHandler {

    private final MemberInquiryService memberInquiryService;

    public FindMemberInfoHandler(MemberInquiryService memberInquiryService) {
        this.memberInquiryService = memberInquiryService;
    }

    @Override
    public ResponseEntity process(String userId) {
        Member member = memberInquiryService.findMemberInfo(userId);
        return new ResponseEntity<Member>(member, HttpStatus.OK);
    }

}
