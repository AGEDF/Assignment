package com.sts.service;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sts.model.Customer;
@Service
public class ApiService {

    private static String token;
    
    
    public ResponseEntity<String> authenticate() {
        String login = "test@sunbasedata.com";
        String password = "Test@123";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"login_id\": \"" + login + "\", \"password\": \"" + password + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://qa2.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp", entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            int startIndex = responseBody.indexOf(":\"") + 2;
            int endIndex = responseBody.lastIndexOf("\"");
            this.token = responseBody.substring(startIndex, endIndex);
        }
        
        return response;
    }


    public ResponseEntity<String> createCustomer(Customer customer) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject json = new JSONObject();
        
        
        json.put("first_name", customer.getFirst_name());
        json.put("last_name", customer.getLast_name());
        json.put("street", customer.getStreet());
        json.put("address", customer.getAddress());
        json.put("city", customer.getCity());
        json.put("state", customer.getState());
        json.put("email", customer.getEmail());
        json.put("phone", customer.getPhone());

        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
        return restTemplate.postForEntity("https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=create", entity, String.class);
    }

    public ResponseEntity<String> getCustomers() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange("https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list", HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<String> deleteCustomer(String uuid) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
            "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=delete&uuid=" + uuid,
            HttpMethod.POST,
            entity,
            String.class
        );
    }


    public ResponseEntity<String> updateCustomer(String uuid, Customer customer) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject json = new JSONObject();
      
        json.put("uuid", customer.getUuid());
        json.put("first_name", customer.getFirst_name());
        json.put("last_name", customer.getLast_name());
        json.put("street", customer.getStreet());        
        json.put("address", customer.getAddress());
        json.put("city", customer.getCity());
        json.put("state", customer.getState());
        json.put("email", customer.getEmail());
        json.put("phone", customer.getPhone());

        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
        return restTemplate.exchange("https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=update&uuid=" + uuid, HttpMethod.POST, entity, String.class);
    }
    public Customer parseCustomerFromApiResponse(String apiResponse) {
        JSONObject json = new JSONObject(apiResponse);

        Customer customer = new Customer(apiResponse,apiResponse, apiResponse, apiResponse, apiResponse, apiResponse, apiResponse, apiResponse, apiResponse);
       
        customer.setUuid(getStringFromJSON(json, "uuid"));
        customer.setFirst_name(getStringFromJSON(json, "first_name"));
        customer.setLast_name(getStringFromJSON(json, "last_name"));
        customer.setStreet(getStringFromJSON(json, "street"));
        customer.setAddress(getStringFromJSON(json, "address"));
        customer.setCity(getStringFromJSON(json, "city"));
        customer.setState(getStringFromJSON(json, "state"));
        customer.setEmail(getStringFromJSON(json, "email"));
        customer.setPhone(getStringFromJSON(json, "phone"));

        return customer;
    }
    private String getStringFromJSON(JSONObject jsonObject, String key) {
        return jsonObject.has(key) && !jsonObject.isNull(key) ? jsonObject.getString(key) : "";
    }

    public ResponseEntity<String> getCustomerByUuid(String uuid) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer&uuid=" + uuid,
                HttpMethod.GET,
                entity,
                String.class
        );
    }

}
