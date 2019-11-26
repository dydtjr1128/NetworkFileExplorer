package com.dydtjr1128.nfe.admin.controller;


import com.dydtjr1128.nfe.admin.model.LoginRequest;
import com.dydtjr1128.nfe.network.Client;
import com.dydtjr1128.nfe.network.ClientManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    @GetMapping("/clients")
    public ResponseEntity<?> authenticateUser() {
        ArrayList<String> clients = new ArrayList<>();
        for (Client client : ClientManager.getInstance().clientsVector) {
            clients.add(client.getClientIP());
        }
        return ResponseEntity.ok(clients);
    }
    @GetMapping("/directory/{ip}/{path}")
    public ResponseEntity<?> authenticateUser(@PathVariable String ip, @PathVariable String path) throws InterruptedException {
        path = path.replace("|","\\");
        if(path.endsWith(":"))
            path += "/";
        log.debug("@@@@@@@@@@@@@@@@@@@@@@@" + ip + "@@" +path + "@@");
        Client client = ClientManager.getInstance().clientsHashMap.get(ip);
        client.getDirectoriesByPath(path);
        String result = client.getBlockingQueue().take();

        return ResponseEntity.ok(result);
    }
}
