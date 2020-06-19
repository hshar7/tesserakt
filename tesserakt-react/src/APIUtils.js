import { API_BASE_URL, ACCESS_TOKEN } from './constants';
import axios from "axios";

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    })
    
    if(localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
    .then(response => 
        response.json().then(json => {
            if(!response.ok) {
                return Promise.reject(json);
            }
            return json;
        })
    );
};

const downloadRequest = (options) => {
    const headers = {
        'Content-Type': 'application/json',
    }

    if(localStorage.getItem(ACCESS_TOKEN)) {
        headers.Authorization = 'Bearer ' + localStorage.getItem(ACCESS_TOKEN);
    }

    const defaults = {headers: headers, responseType: 'arraybuffer'};
    options = Object.assign({}, defaults, options);

    return axios.request(options.url, options)
};

export function login(loginRequest) {
    return request({
        url: API_BASE_URL + "/auth/signin",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function signup(signupRequest) {
    return request({
        url: API_BASE_URL + "/auth/signup",
        method: 'POST',
        body: JSON.stringify(signupRequest)
    });
}

export function checkUsernameAvailability(username) {
    return request({
        url: API_BASE_URL + "/user/checkUsernameAvailability?username=" + username,
        method: 'GET'
    });
}

export function checkEmailAvailability(email) {
    return request({
        url: API_BASE_URL + "/user/checkEmailAvailability?email=" + email,
        method: 'GET'
    });
}

export function getCurrentUser() {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/user/me",
        method: 'GET'
    });
}

export function getCurrentUserHasNotification() {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/notification/have",
        method: 'GET'
    });
}

export function getCurrentUserNotifications() {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/notifications",
        method: 'GET'
    });
}

export function getUserProfileById(id) {
    return request({
        url: API_BASE_URL + "/users/" + id,
        method: 'GET'
    });
}

export function newDeal(requestObject) {
    return request({
        url: API_BASE_URL + "/deal",
        method: 'POST',
        body: JSON.stringify(requestObject)
    });
}

export function editDeal(requestObject) {
    return request({
        url: API_BASE_URL + "/deal/" + requestObject.dealId,
        method: 'PUT',
        body: JSON.stringify(requestObject)
    });
}

export function deleteDeal(dealId) {
    return request({
        url: API_BASE_URL + "/deal/" + dealId,
        method: 'DELETE'
    });
}

export function subscribeToDeal(requestObject) {
    return request({
        url: API_BASE_URL + "/deal/" + requestObject.dealId + "/subscribe",
        method: 'PUT',
        body: JSON.stringify(requestObject)
    });
}

export function unsubscribeFromDeal(dealId) {
    return request({
        url: API_BASE_URL + "/deal/" + dealId + "/subscribe",
        method: 'DELETE'
    });
}

export function getDeal(id) {
    return request({
        url: API_BASE_URL + "/deal/" + id,
        method: 'GET'
    });
}

export function allDeals() {
    return request({
        url: API_BASE_URL + "/deals",
        method: 'GET'
    });
}

export function allDealsByStatus(status) {
    return request({
        url: API_BASE_URL + "/deals-by-status?status=" + status,
        method: 'GET'
    });
}

export function myOpenDeals() {
    return request({
        url: API_BASE_URL + "/my-open-deals",
        method: 'GET'
    });
}

export function myMatchingCriteria() {
    return request({
        url: API_BASE_URL + "/matchingCriteria",
        method: 'GET'
    });
}

export function mySyndicates() {
    return request({
        url: API_BASE_URL + "/syndicate",
        method: 'GET'
    });
}

export function inviteToDeal(email, dealId) {
    return request({
        url: API_BASE_URL + "/deal/" + dealId + "/invite?email=" + email,
        method: 'POST'
    });
}

export function readyUp(dealId) {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/deal/" + dealId + "/readyUp",
        method: 'PUT'
    });
}

export function syndicateRecommendation() {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }
    return request({
        url: API_BASE_URL + "/syndicateRecommendation",
        method: 'GET'
    })
}

export function newMatchingCriteria(requestObject) {
    return request({
        url: API_BASE_URL + "/matchingCriteria",
        method: 'POST',
        body: JSON.stringify(requestObject)
    });
}

export function getDealFiles(dealId) {
    return request({
        url: API_BASE_URL + "/fileManager/" + dealId,
        method: 'GET'
    });
}

export function downloadFile(fullFileName) {
    return downloadRequest({
        url: API_BASE_URL + "/fileManager/" + fullFileName,
        method: 'GET'
    });
}

export function deleteDealFile(fullFileName) {
    return request({
        url: API_BASE_URL + "/fileManager/" + fullFileName,
        method: 'DELETE'
    });
}

export function makeFileSensitive(fullFileName) {
    return request({
        url: API_BASE_URL + "/fileManager/" + fullFileName + "/makeSensitive",
        method: 'POST'
    });
}

export function allUsers() {
    return request({
        url: API_BASE_URL + "/users",
        method: 'GET'
    });
}
