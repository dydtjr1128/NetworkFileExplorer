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
                if (!response.ok) {
                    return Promise.reject(json);
                }
                return json;
            })
        );

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

export function getClients() {
    const url = API_BASE_URL + "/admin/clients"
    const requestData = {
        method: 'GET'
    }
    return request(url, requestData);
}

export function getDirectores(ip, path) {
    const url = API_BASE_URL + "/admin/directory/" + ip + "/" + path.replace(/\\/g, "|");
    const requestData = {
        method: 'GET'
    }
    return request(url, requestData);
}

export function changeFileName(ip, fromPath, toName) {
    const url = API_BASE_URL + "/admin/directory/change/" + ip + "/" + fromPath.replace(/\\/g, "|") + "/" + toName;
    const requestData = {
        method: 'PUT'
    }
    return request(url, requestData);
}

export function deleteFile(ip, deleteFilePath) {
    const url = API_BASE_URL + "/admin/directory/" + ip + "/" + deleteFilePath.replace(/\\/g, "|");
    const requestData = {
        method: 'DELETE'
    }
    return request(url, requestData);
}
export function copyFile(ip, fromPath, toPath) {
    if (ip === '' || fromPath === '' || toPath === '') {
        alert("copyFile err")
    }
    const url = API_BASE_URL + "/admin/directory/copy/" + ip + "/" + fromPath.replace(/\\/g, "|") + "/" + toPath.replace(/\\/g, "|");
    const requestData = {
        method: 'PUT'
    }
    return request(url, requestData);
}

export function moveFile(ip, fromPath, toPath) {
    if (ip === '' || fromPath === '' || toPath === '') {
        alert("moveFile err")
    }
    const url = API_BASE_URL + "/admin/directory/move/" + ip + "/" + fromPath.replace(/\\/g, "|") + "/" + toPath.replace(/\\/g, "|");
    const requestData = {
        method: 'PUT'
    }
    return request(url, requestData);
}



