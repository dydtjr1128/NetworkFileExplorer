import { observable } from 'mobx';

const ClientStore = observable({
    client_list: [],
    currentClientIP:'',
    currentClientPath:'C:\\',
    getParentPath : function(){        
        if(this.isRoot()){
            return this.currentClientPath;
        } else {
            return this.currentClientPath.substring(0, this.currentClientPath.lastIndexOf("\\"));
        }
    },
    isRoot : function(){        
        return this.currentClientPath.substring(1) === ":\\";
    },
    currentDirectoriesList:[],//data
    selectedIP:'',
    selectedPath:'',
    selectedAction:'',
    expanded:[],    
    addExpanded : function (data){
        const index = this.expanded.indexOf(data)
        if(index === 1){
            this.expanded.push(data);
        }
    },
    removeExpanded : function(data){
        const index = this.expanded.indexOf(data)
        if(index !==-1){
            this.expanded.splice(index, 1);
        }
    },
});
// class ClientStore2 {
//     @observable client_list = [];

// }

export default ClientStore;
