import { observable, action } from 'mobx';

const ClientStore = observable({
    client_list: [],
    currentClientIP:'',
    currentClientPath:'',    
    currentDirectoriesList:[],
    expanded:[],
    
    addExpanded : function (data){
        const index = this.expanded.indexOf(data)
        if(index ==-1){
            this.expanded.push(data);
        }
    },
    removeExpanded : function(data){
        const index = this.expanded.indexOf(data)
        if(index !=-1){
            this.expanded.splice(index, 1);
        }
    },
});
// class ClientStore2 {
//     @observable client_list = [];

// }

export default ClientStore;
