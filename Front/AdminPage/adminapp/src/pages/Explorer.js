import React from 'react';
import Browser from 'component/Browser';
import { Redirect } from "react-router-dom";
import { ACCESS_TOKEN } from '../util/Constants';

export default function Explorer(props) {
  return (
    localStorage.getItem(ACCESS_TOKEN)? <Browser /> : <Redirect to='/' />
  );
}