
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import SubscriptionManager from "./components/SubscriptionManager"

import PaymentManager from "./components/PaymentManager"

import UnderwritingManager from "./components/UnderwritingManager"


import SubsciptionView from "./components/subsciptionView"
export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/Subscription',
                name: 'SubscriptionManager',
                component: SubscriptionManager
            },

            {
                path: '/Payment',
                name: 'PaymentManager',
                component: PaymentManager
            },

            {
                path: '/Underwriting',
                name: 'UnderwritingManager',
                component: UnderwritingManager
            },


            {
                path: '/subsciptionView',
                name: 'subsciptionView',
                component: subsciptionView
            },


    ]
})
