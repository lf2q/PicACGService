package service.imp;

import dao.UserMapper;
import entity.User;
import entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.UserService;
import util.KeyBufferManager;
import util.StringTools;

@Service(value = "UserService")
public class UserServiceImp implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public int verify(String name, String password) {
        User user = userMapper.selectByName(name);
        if (user == null) {
            return 1;
        } else if (!password.equals(user.getPassword())) {
            return 2;
        }
        return 0;
    }

    @Override
    public long findIdByName(String name) {
        User user = userMapper.selectByName(name);
        if (user == null) {
            return 0;
        }
        return user.getId();
    }

    @Override
    public int sign_up(String name, String password) {
        User user = userMapper.selectByName(name);
        //账号已存在
        if (user != null) {
            return 2;
        }
        //字符串合法
        if (StringTools.name(name) && StringTools.password(password)) {
            user = new User(name, password);
            userMapper.insert(user);
            return 0;
        }
        //字符串不合法
        return 3;
    }

    @Override
    public int updateName(String name, long key) {
        //登录名合法
        if (StringTools.name(name)) {
            User user = userMapper.selectByName(name);
            //登录名未被占用
            if (user == null) {
                KeyBufferManager manager = KeyBufferManager.getInstance();
                user = manager.getUser(key);
                //修改数据库中的数据
                userMapper.updateName(name, user.getId());
                //修改成功
                return 0;
            }
            return 2;
        }
        return 1;
    }

    @Override
    public int updatePassword(String source, String password, long key) {
        //密码格式合法
        if (StringTools.password(password)) {
            KeyBufferManager manager = KeyBufferManager.getInstance();
            User user = manager.getUser(key);
            //输入的原密码正确
            if (user.getPassword().equals(source)) {
                userMapper.updatePassword(password, user.getId());
                //修改成功
                return 0;
            }
            return 2;
        }
        return 1;
    }

    @Override
    public void updateInfoData(UserInfo info, long key) {
        KeyBufferManager manager = KeyBufferManager.getInstance();
        User user = manager.getUser(key);
        userMapper.updateUserInfo(info, user.getId());
    }
}
