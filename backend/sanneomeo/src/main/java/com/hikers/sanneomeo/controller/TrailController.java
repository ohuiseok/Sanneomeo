package com.hikers.sanneomeo.controller;

import com.hikers.sanneomeo.config.YmlConfig;
import com.hikers.sanneomeo.dto.request.KeepTrailRequestDto;
import com.hikers.sanneomeo.dto.response.BaseResponseDto;
import com.hikers.sanneomeo.dto.response.PathResponseDto;
import com.hikers.sanneomeo.exception.BaseException;
import com.hikers.sanneomeo.exception.BaseResponseStatus;
import com.hikers.sanneomeo.service.TrailService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trail")
@RequiredArgsConstructor
public class TrailController {

    private final TrailService trailService;
    private final YmlConfig ymlConfig;

    @GetMapping("/info/{trailIdx}")
    public BaseResponseDto<?> getTrailInfo(@PathVariable("trailIdx") Long seq) {
        return new BaseResponseDto<>(trailService.getPathsBySequence(seq));
    }


    @PostMapping("/keep")
    public BaseResponseDto<Boolean> keepTrail(@RequestBody KeepTrailRequestDto keepTrailRequestDto) {
        try {
            Long authUserSeq = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            //유저가 해당 trailSeq에 대한 찜 데이터 있다면 get -> true/false 토글
            //없다면 post
            boolean result = trailService.keep(authUserSeq,  keepTrailRequestDto.getCourseSeq());
            return new BaseResponseDto<>(result);
        } catch (Exception e) {
            if (e instanceof BaseException) {
                throw e;
            } else {
                throw new BaseException(BaseResponseStatus.FAIL);
            }

        }
    }

    @GetMapping("/trail/recommend/survey")
    public BaseResponseDto<?> getRecommendTrails(@RequestParam(value="level", required=false) int level,
                                                 @RequestParam(value="region", required=false) String region,
                                                 @RequestParam(value="purpose", required=false) int purpose,
                                                 @RequestParam(value="time", required=false) int time) {

        // level : 1/2/3, region : si(8도), purpose :
        String flaskUrl = ymlConfig.getFlaskEndPoint();

        return null;
    }

}
