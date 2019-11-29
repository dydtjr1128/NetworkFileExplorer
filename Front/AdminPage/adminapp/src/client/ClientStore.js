import { observable } from 'mobx';
import { faShareSquare, faCopy } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

const ClientStore = observable({
    client_list: [],
    currentClientIP: '',
    currentClientPath: 'C:\\',
    getParentPath: function () {
        if (this.isRoot()) {
            return this.currentClientPath;
        } else {
            return this.currentClientPath.substring(0, this.currentClientPath.lastIndexOf("\\"));
        }
    },
    isRoot: function () {
        return this.currentClientPath.substring(1) === ":\\";
    },
    currentDirectoriesList: [],//data
    selectedIP: '',
    selectedPath: '',
    selectedIndex: 0,
    selectedType: '',
    selectedAction: '',
    clearSelectedData: function () {
        this.selectedIP = '';
        this.selectedPath = '';
        this.selectedType = '';
        this.selectedIndex = 0;
        this.selectedAction = '';
    },
    copymoveDataJson : null,
    copymoveData: {
        path: '',
        fileName: '',
        ip: '',
        action: [{
            label: "복사",
            icon: faCopy,
            act: 1,
        }, {
            label: "이동",
            icon: faShareSquare,
            act: 2,
        },]
    },
    clearCopyMoveDatay: function () {
        this.copymoveData.path = ''
        this.copymoveData.fileName = ''
        this.copymoveData.ip = ''
        this.copymoveData.action = [{
            label: "복사",
            icon: faShareSquare,
            act: 1,
        }, {
            label: "이동",
            icon: faCopy,
            act: 2,
        },]

    },
    expanded: [],
    addExpanded: function (data) {
        const index = this.expanded.indexOf(data)
        if (index === 1) {
            this.expanded.push(data);
        }
    },
    removeExpanded: function (data) {
        const index = this.expanded.indexOf(data)
        if (index !== -1) {
            this.expanded.splice(index, 1);
        }
    },
});
// class ClientStore2 {
//     @observable client_list = [];

// }

export default ClientStore;
