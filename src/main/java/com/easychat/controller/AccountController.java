package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.entity.vo.SysSettingVO;
import com.easychat.entity.vo.UserInfoVO;
import com.easychat.exception.BusinessException;
import com.easychat.redis.RedisComponent;
import com.easychat.redis.RedisUtils;
import com.easychat.service.UserContactService;
import com.easychat.service.UserInfoService;
import com.easychat.utils.CopyTools;
import com.easychat.websocket.MessageHandler;
import com.wf.captcha.GifCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("accountController")
@RequestMapping("/account")
@Validated
@Slf4j
public class AccountController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MessageHandler messageHandler;

    @Resource
    private UserContactService userContactService;

    @Resource
    private RedisComponent redisComponet;

    /*
     * 生成验证码
     * */
    @RequestMapping("/checkCode")
    public ResponseVO checkCode() {

        // 创建GifCaptcha对象，设置宽度为100，高度为42
        GifCaptcha gifCaptcha = new GifCaptcha(100, 42);
        // 获取生成的验证码内容
        String captcha_code = gifCaptcha.text();
        String checkCodeKey = UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey, captcha_code, 60 * 10);
        // 获取生成验证码的base64字符串
        String base64 = gifCaptcha.toBase64();
        Map<String, String> result = new HashMap<>();
        result.put("checkCode", base64);
        result.put("checkCodeKey", checkCodeKey);
        return getSuccessResponseVO(result);
    }

    /*
     * 注册
     * */
    @RequestMapping("/register")
    public ResponseVO register(@NotEmpty String checkCodeKey,
                               @NotEmpty @Email String email,
                               @NotEmpty String password,
                               @NotEmpty String nickName,
                               @NotEmpty String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey))) {
                throw new BusinessException("验证码错误");
            }
            userInfoService.register(email, nickName, password);
            return getSuccessResponseVO(null);
        } finally {
            redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
        }

    }

    /*
     * 登录
     * */
    @RequestMapping("/login")
    public ResponseVO login(@NotEmpty String checkCodeKey,
                            @NotEmpty @Email String email,
                            @NotEmpty String password,
                            @NotEmpty String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey))) {
                throw new BusinessException("图片验证码不正确");
            }
            UserInfoVO userInfoVO = userInfoService.login(email, password);
            return getSuccessResponseVO(userInfoVO);
        } finally {
            redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
        }
    }

    @RequestMapping( "/getSysSetting")
    @GlobalInterceptor
    public ResponseVO getSysSetting() {
        SysSettingDto sysSettingDto = redisComponet.getSysSetting();
        return getSuccessResponseVO(CopyTools.copy(sysSettingDto, SysSettingVO.class));
    }
}