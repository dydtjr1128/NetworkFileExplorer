package com.dydtjr1128.nfe.admin.controller;


import com.dydtjr1128.nfe.admin.model.LoginRequest;
import com.dydtjr1128.nfe.network.Client;
import com.dydtjr1128.nfe.network.ClientManager;
import com.dydtjr1128.nfe.protocol.core.BindingData;
import com.dydtjr1128.nfe.protocol.core.NFEProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    public BindingData getCorrectProtocol(Client client, HashSet<Byte> requestSet) throws InterruptedException {
        BindingData result;
        do {
            result = client.getBlockingQueue().take();
            if (!requestSet.contains(result.getProtocol()))
                client.getBlockingQueue().add(result);
            else
                break;
        } while (true);
        return result;
    }

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
        long start = System.currentTimeMillis();
        path = path.replace("|", "\\");
        if (path.endsWith(":"))
            path += "/";
        log.debug("[GET] Get file list : " + ip + "@" + path);
        Client client = ClientManager.getInstance().clientsHashMap.get(ip);
        client.getDirectoriesByPath(path);

        HashSet<Byte> set = new HashSet<Byte>();
        set.add(NFEProtocol.GET_LIST);
        BindingData result = getCorrectProtocol(client, set);

        System.out.println("!!!!!!!!!!!!!!!" + (System.currentTimeMillis() - start));

        return ResponseEntity.ok(result.getPayload());
    }

    @PutMapping("/directory/{ip}/{fromPath}/{name}")
    public ResponseEntity<?> authenticateUser(@PathVariable String ip, @PathVariable String fromPath, @PathVariable String name) throws InterruptedException {
        long start = System.currentTimeMillis();
        fromPath = fromPath.replace("|", "\\");
        if (fromPath.endsWith(":"))
            fromPath += "/";
        log.debug("[PUT] Change file name : " + ip + "@" + fromPath + "@" + name);
        Client client = ClientManager.getInstance().clientsHashMap.get(ip);
        client.changeFileName(fromPath + "|" + name);

        HashSet<Byte> set = new HashSet<Byte>();
        set.add(NFEProtocol.REQUEST_OK);
        set.add(NFEProtocol.REQUEST_FAIL);
        BindingData result = getCorrectProtocol(client, set);

        System.out.println("!!!!!!!!!!!!!!!" + (System.currentTimeMillis() - start));
        if (result.getProtocol() == NFEProtocol.REQUEST_OK)
            return ResponseEntity.ok(result);
        else
            return ResponseEntity.badRequest().body("Invalid path/file");
    }
}
