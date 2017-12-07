package club.zhcs.thunder.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.nutz.dao.Cnd;
import org.nutz.img.Images;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import club.zhcs.common.Result;
import club.zhcs.thunder.bean.acl.User;
import club.zhcs.thunder.biz.acl.UserService;
import club.zhcs.thunder.config.qiniu.QiniuAutoConfiguration.QiniuUploader;
import club.zhcs.thunder.config.qiniu.QiniuAutoConfiguration.R;

/**
 * @author kerbores
 *
 */
@Controller
@RequestMapping("image")
public class ImageController {
	@Autowired
	QiniuUploader qiniuUploader;

	@Autowired
	UserService userService;

	@PostMapping("upload")
	@ResponseBody
	public Result test(MultipartFile file) throws IOException {
		R r = qiniuUploader.upload(file.getInputStream());
		return r == null ? Result.fail("上传失败!") : Result.success().addData("url", r.getUrl());
	}

	@GetMapping("avatar")
	public void avatar(@RequestParam(value = "id", required = false, defaultValue = "0") long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = null;
		if (id == 0) {
			user = userService.fetch(Cnd.where("name", "=", SecurityUtils.getSubject().getPrincipal()));
		} else {
			user = userService.fetch(id);
		}

		Images.write(Images.createAvatar(user == null ? "N" : user.getNickName()), "png", response.getOutputStream());
	}
}
