package com.dydtjr1128.nfe.admin.controller;

import com.dydtjr1128.nfe.admin.model.EmptyJsonResponse;
import com.dydtjr1128.nfe.protocol.core.BindingData;
import com.dydtjr1128.nfe.protocol.core.NFEProtocol;
import com.dydtjr1128.nfe.server.Client;
import com.dydtjr1128.nfe.server.ClientManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    @GetMapping("/clients")
    public ResponseEntity<?> getUserLists() {
        ArrayList<String> clients = ClientManager.getInstance().getAllClients();
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
        BindingData result = client.getCorrectProtocol(client, set);

        log.debug(String.valueOf((System.currentTimeMillis() - start)));
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
        BindingData result = client.getCorrectProtocol(client, set);

        log.debug(String.valueOf((System.currentTimeMillis() - start)));
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
        BindingData result = client.getCorrectProtocol(client, set);

        log.debug(String.valueOf((System.currentTimeMillis() - start)));
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
        BindingData result = client.getCorrectProtocol(client, set);

        log.debug(String.valueOf((System.currentTimeMillis() - start)));
        if (result.getPayload().equals("s"))
            return ResponseEntity.ok(new EmptyJsonResponse());
        else
            return ResponseEntity.badRequest().body(new EmptyJsonResponse());
    }

    @DeleteMapping("/directory/{ip}/{filePath}")
    public ResponseEntity<?> deleteFile(@PathVariable String ip, @PathVariable String filePath) throws InterruptedException {
        long start = System.currentTimeMillis();
        filePath = pathPreProcessing(filePath);
        Client client = ClientManager.getInstance().clientsHashMap.get(ip);
        client.deleteFile(filePath);
        log.debug("[DELETE] Delete file path : " + ip + "@" + filePath);

        HashSet<Byte> set = new HashSet<Byte>();
        set.add(NFEProtocol.DELETE);
        BindingData result = client.getCorrectProtocol(client, set);

        log.debug(String.valueOf((System.currentTimeMillis() - start)));
        if (result.getPayload().equals("s"))
            return ResponseEntity.ok(new EmptyJsonResponse());
        else
            return ResponseEntity.badRequest().body(new EmptyJsonResponse());
    }

    @PostMapping("/upload/{ip}/{serverFilePath}/{clientFilePath}")
    public ResponseEntity<?> fileTransferServer2Client(@PathVariable String ip, @PathVariable String serverFilePath, @PathVariable String clientFilePath) {
        long start = System.currentTimeMillis();
        //AsyncFileServer -> TCP -> Client
        serverFilePath = pathPreProcessing(serverFilePath);
        clientFilePath = pathPreProcessing(clientFilePath);
        log.debug("[UPLOAD] Upload file : " + ip + "@" + serverFilePath + "@" + clientFilePath);

        Client client = ClientManager.getInstance().clientsHashMap.get(ip);
        client.uploadToClient(serverFilePath, clientFilePath);

        log.debug(String.valueOf((System.currentTimeMillis() - start)));
        return ResponseEntity.ok(new EmptyJsonResponse());
    }

    @GetMapping("/download/{ip}/{clientFilePath}")
    public ResponseEntity<?> fileTransferClient2Server(@PathVariable String ip, @PathVariable String clientFilePath) {
        long start = System.currentTimeMillis();
        //Server -> TCP -> Client -> TCP -> AsyncFileServer
        System.out.println(clientFilePath);
        clientFilePath = pathPreProcessing(clientFilePath);
        System.out.println(clientFilePath);
        log.debug("[Download] Upload file : " + ip + "@" + clientFilePath);
        //고정된 폴더에 저장
        Client client = ClientManager.getInstance().clientsHashMap.get(ip);
        client.downloadFromClient(clientFilePath);

        log.debug(String.valueOf((System.currentTimeMillis() - start)));
        return ResponseEntity.ok(new EmptyJsonResponse());
    }

    private String pathPreProcessing(String path) {
        path = path.replace("|", "/");
        if (path.endsWith(":"))
            path += "/";
        return path;
    }
}
