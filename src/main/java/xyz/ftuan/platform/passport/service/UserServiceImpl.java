package xyz.ftuan.platform.passport.service;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.ftuan.platform.passport.domain.User;
import xyz.ftuan.platform.passport.domain.UserMapper;
import xyz.ftuan.platform.passport.exception.ServiceException;
import xyz.ftuan.platform.passport.model.RegisterRequest;
import xyz.ftuan.platform.passport.model.UserProfile;
import xyz.ftuan.platform.passport.util.TimeUtils;

/**
 * Created by LUOXC on 2017/2/26.
 */
@Service
public class UserServiceImpl implements UserService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserMapper userMapper;

	@Override
	public void register(RegisterRequest request) {
		validateMobile(request);
		User user = new User();
		user.setMobile(request.getMobile());
		user.setPassword(request.getMD5Password());
		user.setNickname(request.getNickname());
		user.setCreateTime(TimeUtils.currentTimeSeconds());
		userMapper.insert(user);
		logger.info( "register success, the mobile is {}.", request.getMobile());
	}

	private void validateMobile(RegisterRequest request) {
		if (StringUtils.isEmpty(request.getMobile())) {
			logger.info( "register fail, mobile is empty.");
			throw new ServiceException(10000, "mobile is empty.");
		}
		User user = userMapper.selectByMobile(request.getMobile());
		if (Objects.nonNull(user)) {
			logger.info( "register fail, the account has exist of the mobile {}.", request.getMobile());
			throw new ServiceException(10001, "account is exist.");
		}
	}

	public void login(RegisterRequest request) {
		if (StringUtils.isEmpty(request.getMobile())) {
			throw new ServiceException(10001, "mobile is empty.");
		}
		User user = userMapper.selectByMobile(request.getMobile());
		if (Objects.isNull(user)) {
			logger.info( "login fail, the account is not exist of the mobile {}.", request.getMobile());
			throw new ServiceException(10000, "account or password error");
		}
		if (!Objects.equals(user.getPassword(), request.getMD5Password())) {
			logger.info( "login fail, the password is wrong of the user {}.", request.getMobile());
			throw new ServiceException(10000, "account or password error");
		}
		logger.info("login success of the user {}.", request.getMobile());
	}

	@Override
	public UserProfile findUserById(Long id) {
		User user = userMapper.selectById(id);
		return UserProfile.newUserProfile(user);
	}
}