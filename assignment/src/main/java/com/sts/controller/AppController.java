package com.sts.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sts.model.Customer;
import com.sts.service.ApiService;

@Controller
@RequestMapping("/app")
public class AppController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String authenticate(@RequestParam String username, @RequestParam String password, Model model) {
        ResponseEntity<String> response = apiService.authenticate();
        if (response.getStatusCode().is2xxSuccessful()) {
            return "redirect:/app/customers";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    @GetMapping("/customers")
    public String listCustomers(Model model) {
        ResponseEntity<String> response = apiService.getCustomers();
        if (response.getStatusCode().is2xxSuccessful()) {
            List<Customer> customers = parseCustomerList(response.getBody()); 
            Customer first = customers.get(0);
            first.getUuid();
            model.addAttribute("customers", customers);
        }
        return "customer-list";
    }

    @GetMapping("/customer/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new Customer("","", "", "", "", "", "", "", ""));
        return "add-customer";
    }


    @PostMapping("/customer/add")
    public String createCustomer(@ModelAttribute Customer customer, Model model) {
        ResponseEntity<String> response = apiService.createCustomer(customer);
        if (response.getStatusCode().is2xxSuccessful()) {
            return "redirect:/app/customers";
        } else {
            model.addAttribute("error", "Failed to create customer");
            return "add-customer";
        }
    }
    @PostMapping("/customer/delete/{uuid}")
    public String deleteCustomer(@PathVariable String uuid) {
        ResponseEntity<String> response = apiService.deleteCustomer(uuid);
        return "redirect:/app/customers";
    }
    
    @PostMapping("/customer/update/{uuid}")
    public String showUpdateForm(@PathVariable String uuid, Model model) {
        ResponseEntity<String> response = apiService.getCustomerByUuid(uuid);
        
        if (response.getStatusCode().is2xxSuccessful()) {
           
            JSONObject customerObject = new JSONObject(response.getBody());
            Customer customer = new Customer(
                getStringFromJSON(customerObject, "uuid"),
                getStringFromJSON(customerObject, "first_name"),
                getStringFromJSON(customerObject, "last_name"),
                getStringFromJSON(customerObject, "street"),
                getStringFromJSON(customerObject, "address"),
                getStringFromJSON(customerObject, "city"),
                getStringFromJSON(customerObject, "state"),
                getStringFromJSON(customerObject, "email"),
                getStringFromJSON(customerObject, "phone")
            );
            model.addAttribute("customer", customer);
            return "update-customer";
        } else {
            return "redirect:/app/customers";
        }
    }
   

    private List<Customer> parseCustomerList(String responseBody) {
        List<Customer> customers = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(responseBody);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject customerObject = jsonArray.getJSONObject(i);

            Customer customer = new Customer(
            	
            	getStringFromJSON(customerObject, "uuid"),
                getStringFromJSON(customerObject, "first_name"),
                getStringFromJSON(customerObject, "last_name"),
                getStringFromJSON(customerObject, "street"),
                getStringFromJSON(customerObject, "address"),
                getStringFromJSON(customerObject, "city"),
                getStringFromJSON(customerObject, "state"),
                getStringFromJSON(customerObject, "email"),
                getStringFromJSON(customerObject, "phone")
            );

            customers.add(customer);
        }
        Customer first = customers.get(0);
        first.getUuid();

        return customers;
    }



    private String getStringFromJSON(JSONObject jsonObject, String key) {
        return jsonObject.has(key) && !jsonObject.isNull(key) ? jsonObject.getString(key) : "";
    }

}
