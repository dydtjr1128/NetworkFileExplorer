import { observable } from 'mobx';

const ClientStore = observable({
    client_list: [],
    currentClientIP:'',
    currentClientPath:'',    
    currentDirectoriesList:[]
});

export default ClientStore;
