package org.csu.mypetstore.controller;


import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Product;
import org.csu.mypetstore.domain.RandomValidateCode;
import org.csu.mypetstore.service.AccountService;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/account/")
@SessionAttributes({"account", "myList", "authenticated"})
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CatalogService catalogService;

    private static final List<String> LANGUAGE_LIST;
    private static final List<String> CATEGORY_LIST;

    static {
        List<String> langList = new ArrayList<String>();
        langList.add("ENGLISH");
        langList.add("CHINESE");
        LANGUAGE_LIST = Collections.unmodifiableList(langList);

        List<String> catList = new ArrayList<String>();
        catList.add("FISH");
        catList.add("DOGS");
        catList.add("REPTILES");
        catList.add("CATS");
        catList.add("BIRDS");

        CATEGORY_LIST = Collections.unmodifiableList(catList);
    }

    @RequestMapping(value="/checkCode")
    public void getVerify(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
        response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        RandomValidateCode randomValidateCode = new RandomValidateCode();
        try {
            randomValidateCode.getRandcode(request, response);//输出验证码图片方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @GetMapping("signonForm")
    public String signonForm() {
        return "account/signon";
    }

    @PostMapping("signon")
    public String signon(String username, String password, String inputStr, Model model,HttpSession session) {
        Account account = accountService.getAccount(username, password);
        String random = (String) session.getAttribute("RANDOMVALIDATECODEKEY");

        if (account == null) {
            String msg = "Invalid username or password.  Sign on failed.";
            model.addAttribute("msg", msg);
            return "account/signon";
        }
        else if(!random.equals(inputStr)){
            System.out.println(random+"  "+inputStr+"  "+username);
            String msg= "Verification code error";
            model.addAttribute("msg", msg);
            return "account/signon";
        }
        else {
            account.setPassword(null);
            List<Product> myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
            boolean authenticated = true;
            model.addAttribute("account", account);
            model.addAttribute("myList", myList);
            model.addAttribute("authenticated", authenticated);
            return "catalog/main";
        }
    }

    @GetMapping("signoff")
    public String signoff(Model model) {
        Account loginAccount = new Account();
        List<Product> myList = null;
        boolean authenticated = false;
        model.addAttribute("account", loginAccount);
        model.addAttribute("myList", myList);
        model.addAttribute("authenticated", authenticated);
        return "catalog/main";
    }

    @GetMapping("editAccountForm")
    public String editAccountForm(@SessionAttribute("account") Account account , Model model) {
        model.addAttribute("account", account);
        model.addAttribute("LANGUAGE_LIST", LANGUAGE_LIST);
        model.addAttribute("CATEGORY_LIST", CATEGORY_LIST);
        return "account/edit_account";
    }

    @PostMapping("editAccount")
    public String editAccount(Account account, String repeatedPassword, Model model) {
        if (account.getPassword() == null || account.getPassword().length() == 0 || repeatedPassword == null || repeatedPassword.length() == 0) {
            String msg = "密码不能为空";
            model.addAttribute("msg", msg);
            return "account/edit_account";
        } else if (!account.getPassword().equals(repeatedPassword)) {
            String msg = "两次密码不一致";
            model.addAttribute("msg", msg);
            return "account/edit_account";
        } else {
            accountService.updateAccount(account);
            account = accountService.getAccount(account.getUsername());
            List<Product> myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
            boolean authenticated = true;
            model.addAttribute("account", account);
            model.addAttribute("myList", myList);
            model.addAttribute("authenticated", authenticated);
            return "redirect:/catalog/view";
        }
    }

    @GetMapping("newAccountForm")
    public String newAccountForm(HttpSession session, Model model){
//        Account account=(Account)session.getAttribute("account");
//        model.addAttribute("account",account);
        model.addAttribute("account",new Account());
        model.addAttribute("LANGUAGE_LIST", LANGUAGE_LIST);
        model.addAttribute("CATEGORY_LIST", CATEGORY_LIST);
        return "account/new_account";
    }

    @PostMapping("newAccount")
    public String newAccount(Account account, String repeatedPassword, Model model) {
        if(accountService.getAccount(account.getUsername())!=null)
        {
            String msg="用户名已存在";
            boolean authenticated = false;
            model.addAttribute("authenticated", authenticated);
            model.addAttribute("msg",msg);
            return "account/new_account";
        }
        if (account.getPassword() == null || account.getPassword().length() == 0 || repeatedPassword == null || repeatedPassword.length() == 0) {
            String msg = "密码不能为空";
            boolean authenticated = false;
            model.addAttribute("authenticated", authenticated);
            model.addAttribute("msg", msg);
            return "account/new_account";
        } else if (!account.getPassword().equals(repeatedPassword)) {
            String msg = "两次密码不一致";
            boolean authenticated = false;
            model.addAttribute("msg", msg);
            model.addAttribute("authenticated", authenticated);
            return "account/new_account";
        } else {
            accountService.insertAccount(account);
           account = accountService.getAccount(account.getUsername());
          //List<Product> myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
            boolean authenticated = false;
            model.addAttribute("account", account);
            //model.addAttribute("myList", myList);
            model.addAttribute("authenticated", authenticated);
            return "redirect:/account/signonForm";
        }
    }

}

