package org.launchcode.controllers;

import org.launchcode.forms.AddMenuItemForm;
import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Created by Venu chenchu on 7/3/18.
 */
@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value="")
    public String index(Model model) {

        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Available Menus");

        return "menu/index";
    }

    @RequestMapping(value="add", method = RequestMethod.GET)
    public String displayAddMenu(Model model) {
        model.addAttribute("title", "Add A Menu");
        model.addAttribute("menu", new Menu());

        return "menu/add";
    }

    @RequestMapping(value="add", method = RequestMethod.POST)
    public String processAddMenu(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add A Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value="view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu",menu);

        return "menu/view";
    }

    @RequestMapping(value="add-item/{menuId}", method = RequestMethod.GET)
    public String addToMenu(Model model, @PathVariable int menuId) {

        Menu menuAdd = menuDao.findOne(menuId);
        Iterable<Cheese> allCheeses = cheeseDao.findAll();
        AddMenuItemForm form = new AddMenuItemForm(menuAdd, allCheeses);
        model.addAttribute("title","Add item to menu: " + menuAdd.getName());
        model.addAttribute("form",form);

        return "menu/add-item";
    }

    @RequestMapping(value="add-item", method = RequestMethod.POST)
    public String AddItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("form", form);
            return "menu/add-item";
        }

        Menu theMenu = menuDao.findOne(form.getMenuId());
        Cheese toAddCheese = cheeseDao.findOne(form.getCheeseId());
        theMenu.addItem(toAddCheese);

        menuDao.save(theMenu);
        return "redirect:/menu/view/" + theMenu.getId();

    }
}
