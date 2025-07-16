package com.lgcms.auth.remote.member;

import com.lgcms.auth.remote.member.dto.MemberRequest.SignupRequest;
import com.lgcms.auth.remote.member.dto.MemberResponse.SignupResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "RemoteMemberService",
    path = "/backend/members"
)
public interface RemoteMemberService {
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request);
}
