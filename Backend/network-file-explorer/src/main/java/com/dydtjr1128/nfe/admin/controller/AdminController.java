package com.dydtjr1128.nfe.admin.controller;


import com.dydtjr1128.nfe.admin.model.EmptyJsonResponse;
import com.dydtjr1128.nfe.admin.model.LoginRequest;
import com.dydtjr1128.nfe.network.Client;
import com.dydtjr1128.nfe.network.ClientManager;
import com.dydtjr1128.nfe.protocol.core.BindingData;
import com.dydtjr1128.nfe.protocol.core.NFEProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    public String pathPreProcessing(String path) {
        path = path.replace("|", "\\");
        if (path.endsWith(":"))
            path += "/";
        return path;
    }

    @GetMapping("/clients")
    public ResponseEntity<?> getUserLists() {
        ArrayList<String> clients = new ArrayList<>();
        for (Client client : ClientManager.getInstance().clientsVector) {
            clients.add(client.getClientIP());
        }
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/directory/{ip}/{path}")
    public ResponseEntity<?> getFileList(@PathVariable String ip, @PathVariable String path) throws InterruptedException {
        long start = System.currentTimeMillis();
        path = pathPreProcessing(path);
        log.debug("[GET] Get file list : " + ip + "@" + path);
        Client client = ClientManager.getInstance().clientsHashMap.get(ip);
        client.getDirectoriesByPath(path);

        HashSet<Byte> set = new HashSet<Byte>();
        set.add(NFEProtocol.GET_LIST);
        BindingData result = getCorrectProtocol(client, set);

        System.out.println("!!!!!!!!!!!!!!!" + (System.currentTimeMillis() - start));

        return ResponseEntity.ok(result.getPayload());
    }

    @PutMapping("/directory/change/{ip}/{fromPath}/{name}")
    public ResponseEntity<?> changeFileName(@PathVariable String ip, @PathVariable String fromPath, @PathVariable String name) throws InterruptedException {
        long start = System.currentTimeMillis();
        fromPath = pathPreProcessing(fromPath);
        log.debug("[PUT] Change file name : " + ip + "@" + fromPath + "@" + name);
        Client client = ClientManager.getInstance().clientsHashMap.get(ip);
        client.changeFileName(fromPath + "|" + name);

        HashSet<Byte> set = new HashSet<Byte>();
        set.add(NFEProtocol.CHANGE_NAME);
        BindingData result = getCorrectProtocol(client, set);

        if (result.getPayload().equals("s"))
            return ResponseEntity.ok(new EmptyJsonResponse());
        else
            return ResponseEntity.badRequest().body(new EmptyJsonResponse());
    }

    @PutMapping("/directory/copy/{ip}/{fromPath}/{toPath}")
    public ResponseEntity<?> copyFile(@PathVariable String ip, @PathVariable String fromPath, @PathVariable String toPath) throws InterruptedException {
        long start = System.currentTimeMillis();
        fromPath = pathPreProcessing(fromPath);
        toPath = pathPreProcessing(toPath);
        log.debug("[PUT] Copy file : " + ip + "@" + fromPath + "@" + toPath);
        Client client = ClientManager.getInstance().clientsHashMap.get(ip);
        client.copyFile(fromPath + "|" + toPath);

        HashSet<Byte> set = new HashSet<Byte>();
        set.add(NFEProtocol.COPY);
        BindingData result = getCorrectProtocol(client, set);

        if (result.getPayload().equals("s"))
            return ResponseEntity.ok(new EmptyJsonResponse());
        else
            return ResponseEntity.badRequest().body(new EmptyJsonResponse());
    }

    @PutMapping("/directory/move/{ip}/{fromPath}/{toPath}")
    public ResponseEntity<?> moveFile(@PathVariable String ip, @PathVariable String fromPath, @PathVariable String toPath) throws InterruptedException {
        long start = System.currentTimeMillis();
        fromPath = pathPreProcessing(fromPath);
        toPath = pathPreProcessing(toPath);
        log.debug("[PUT] Move file : " + ip + "@" + fromPath + "@" + toPath);
        Client client = ClientManager.getInstance().clientsHashMap.get(ip);
        client.moveFile(fromPath + "|" + toPath);

        HashSet<Byte> set = new HashSet<Byte>();
        set.add(NFEProtocol.MOVE);
        BindingData result = getCorrectProtocol(client, set);

        if (result.getPayload().equals("s"))
            return ResponseEntity.ok(new EmptyJsonResponse());
        else
            return ResponseEntity.badRequest().body(new EmptyJsonResponse());
    }

    @DeleteMapping("/directory/{ip}/{filePath}")
    public ResponseEntity<?> deleteFile(@PathVariable String ip, @PathVariable String filePath) throws InterruptedException {
        long start = System.currentTimeMillis();
        filePath = pathPreProcessing(filePath);
        log.debug("[DELETE] Delete file path : " + ip + "@" + filePath);
        Client client = ClientManager.getInstance().clientsHashMap.get(ip);
        client.deleteFile(filePath);

        HashSet<Byte> set = new HashSet<Byte>();
        set.add(NFEProtocol.DELETE);
        BindingData result = getCorrectProtocol(client, set);

        System.out.println("!!!!!!!!!!!!!!!" + (System.currentTimeMillis() - start));
        if (result.getPayload().equals("s"))
            return ResponseEntity.ok(new EmptyJsonResponse());
        else
            return ResponseEntity.badRequest().body(new EmptyJsonResponse());
    }
}
