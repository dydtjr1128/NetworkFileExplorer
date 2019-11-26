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
