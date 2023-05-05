package com.resdii.vars.controller.postController;

import com.resdii.ms.common.utils.RestUtils;
import com.resdii.vars.enums.BotStatus;
import com.resdii.vars.services.postService.PostService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ANSI.
 */

@RestController
@RequestMapping("/post")
public class PostController {
    PostService postService;


    @GetMapping("/getLinks")
    public ResponseEntity getLinks(@RequestParam String baseUrl, @RequestParam String postType) {
        postService.runGetLinks(baseUrl, postType, 1);
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/getDetail")
    public ResponseEntity getDetail(@RequestParam String baseUrl, @RequestParam String postType) {
        postService.runGetDetail(baseUrl, postType, -1, -1);
        return RestUtils.responseOk(BotStatus.STARTED);
    }



//    @GetMapping("/runGetLinksFailed")
//    public ResponseEntity runGetLinksFailed(@RequestParam String baseUrl) {
//        postService.runGetLinksFailed(baseUrl);
//        return RestUtils.responseOk(BotStatus.STARTED);
//    }
//
//    @GetMapping("/runGetDetailFailedCase")
//    public ResponseEntity runGetDetailFailedCase(@RequestParam int numOfItems,@RequestParam int numOfThreadPerKey, @RequestParam String baseUrl) {
//        postService.runFailedCase(numOfItems, numOfThreadPerKey, baseUrl);
//        return RestUtils.responseOk(BotStatus.STARTED);
//    }
//
//    @GetMapping("/runTryCase")
//    public ResponseEntity runTryCase(@RequestParam int numOfItems,@RequestParam int numOfThreadPerKey, @RequestParam String baseUrl) {
//        postService.runTryCase(numOfItems, numOfThreadPerKey, baseUrl);
//        return RestUtils.responseOk(BotStatus.STARTED);
//    }

    @GetMapping("/runUpdate")
    public ResponseEntity runUpdate() {
        postService.updatePost();
        return RestUtils.responseOk(BotStatus.STARTED);
    }

//    @GetMapping("/runFilter")
//    public ResponseEntity runFilter() {
//        postService.postMAFilter();
//        return RestUtils.responseOk(BotStatus.STARTED);
//    }
//
//    @SneakyThrows
//    @GetMapping("/runTestCase")
//    public ResponseEntity runTestCase(@RequestParam String url, @RequestParam String baseUrl) {
//        postService.runTestCase(url, baseUrl);
//        return RestUtils.responseOk(BotStatus.STARTED);
//    }

    @Autowired
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

}
