import { API_BASE_URL, ACCESS_TOKEN } from './Constants';

const request = (url, options) => {
    const headers = new Headers();
    headers.append('Content-type', 'application/json')

    if (localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', "Bearer " + localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = { headers: headers };
    options = Object.assign({}, defaults, options);
    return fetch(url, options)
        .then(response =>
            response.json().then(json => {
                console.log(response)
                if (!response.ok) {
                    return Promise.reject(json);
                }
                return json;
            })
        ).catch(error => {
            if (error.status === 401) {
                localStorage.removeItem(ACCESS_TOKEN)
                document.location.href = "/"
                alert('Not authenticated')
            }
        });

};

export function login(id, pw) {
    const url = API_BASE_URL + "/auth/login"
    const requestData = {
        method: 'POST',
        body: JSON.stringify({
            id: id,
            password: pw,
        })
    }
    return request(url, requestData);
}

export function validationToken() {
    const url = API_BASE_URL + "/auth/token"
    const requestData = {
        method: 'GET'
    }
    return request(url, requestData);
}


export function getClients() {
    const url = API_BASE_URL + "/admin/clients"
    const requestData = {
        method: 'GET'
    }
    return request(url, requestData);
}

function ipPortToIp(ipPort){
    return ipPort//.substring(0,ipPort.indexOf(":"))
}

function pathFormatting(path){
    return path.replace(/\\/g, "|").replace(/\//g,"|");
}

export function getDirectores(ip, path) {
    const url = API_BASE_URL + "/admin/directory/" + ipPortToIp(ip) + "/" + pathFormatting(path);
    console.log("get d ! : " + path + " " + url)
    const requestData = {
        method: 'GET'
    }
    return request(url, requestData);
}

export function changeFileName(ip, fromPath, toName) {
    const url = API_BASE_URL + "/admin/directory/change/" + ipPortToIp(ip) + "/" + pathFormatting(fromPath) + "/" + toName;
    const requestData = {
        method: 'PUT'
    }
    return request(url, requestData);
}

export function deleteFile(ip, deleteFilePath) {
    const url = API_BASE_URL + "/admin/directory/" + ipPortToIp(ip) + "/" + pathFormatting(deleteFilePath);
    const requestData = {
        method: 'DELETE'
    }
    return request(url, requestData);
}
export function copyFile(ip, fromPath, toPath) {
    ip = ipPortToIp(ip);
    if (ip === '' || fromPath === '' || toPath === '') {
        alert("copyFile err")
    }
    const url = API_BASE_URL + "/admin/directory/copy/" + ip + "/" + pathFormatting(fromPath) + "/" + pathFormatting(toPath);
    const requestData = {
        method: 'PUT'
    }
    return request(url, requestData);
}

export function moveFile(ip, fromPath, toPath) {
    ip = ipPortToIp(ip);
    if (ip === '' || fromPath === '' || toPath === '') {
        alert("moveFile err")
    }
    const url = API_BASE_URL + "/admin/directory/move/" + ip + "/" + pathFormatting(fromPath) + "/" + pathFormatting(toPath);
    const requestData = {
        method: 'PUT'
    }
    return request(url, requestData);
}

export function fileTransferServer2Client(ip, serverPath, clientPath) {
    ip = ipPortToIp(ip);
    if (ip === '' || serverPath === '' || clientPath === '') {
        alert("fileTransferServer2Client err")
    }
    if (serverPath.charAt(0) === "\\") {
        serverPath = serverPath.substring(1);
    }
    const url = API_BASE_URL + "/admin/upload/" + ip + "/" + pathFormatting(serverPath) + "/" + pathFormatting(clientPath);
    const requestData = {
        method: 'POST'
    }
    return request(url, requestData);
}

export function fileTransferClient2Server(ip, clientPath) {
    ip = ipPortToIp(ip);
    if (ip === '' || clientPath === '') {
        alert("fileTransferClient2Server err")
    }
    const url = API_BASE_URL + "/admin/download/" + ip + "/" + pathFormatting(clientPath);
    const requestData = {
        method: 'GET'
    }
    return request(url, requestData);
}



