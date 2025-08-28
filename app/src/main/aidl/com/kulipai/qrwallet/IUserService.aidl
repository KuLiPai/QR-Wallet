package com.kulipai.qrwallet;

import com.kulipai.qrwallet.ShellResult;

interface IUserService {
    ShellResult exec(String cmd);
    void destroy();
    void exit();
}
