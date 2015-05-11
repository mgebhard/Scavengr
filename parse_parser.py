import json
from pprint import pprint
import matplotlib.pyplot as plt
from datetime import datetime
from datetime import date
import sys 

import numpy as np

class ParsePlotter:
    def __init__(self, granularity = "hr", us = False):
        #event names: u'start-hunt', u'create_hunt', u'AppOpened', u'end-hunt', u'user-login'
        self.granularity = granularity
        self.event_to_time = {}
        self.event_to_tally = {}
        self.us = set(['25b9c9b8d9cb2addae680e06', '76b57dc006b6e06fca779c6e', '202106598d84675859d4a82a', '0156c998e26e9c2934782fc1', '104a62dc15cb5c751407cb02'])
        us_counter = 0
        other_counter = 0
        with open("./all_parse_scavengr_data.csv") as data_file:
            for line in data_file:
                data = json.loads(line)
                dims = json.loads(data[u'Dimensions'])
                if (not us) and (dims.get("userId", "") in self.us):
                    us_counter += 1
                    continue
                elif (not us):
                    other_counter += 1
                event_name = data["Event Name"]
                self.event_to_time[event_name] = self.event_to_time.get(event_name, []) + [float(data[u'Timestamp (s)'])]
        for ev in self.event_to_time:
            ti = self.event_to_time[ev]
            self.event_to_tally[ev] = self.tally_times(ti, granularity)
        if not us:
            print "data that's us: ", us_counter
            print "data that's not us: ", other_counter
    #def plot_event(self, e_name):
    #    times = event_to_time[e_name]
    #    title = {u'start-hunt': "Hunts Started", u'create_hunt': "Hunts Created", u'AppOpened': "App-Opens", u'end-hunt': "Hunts Completed", u'user-login': "User Logins "}[e_name]
    def plot_multiple_events(self, e_names, title, x_label = "", y_label = ""):
        plt.figure(1)
        plt.subplot(111)
        color_dict = {u'start-hunt': "red", u'create_hunt': "blue", u'AppOpened': "green", u'end-hunt': "black", u'user-login': "magenta"}
        for ename in e_names:
            tally = self.event_to_tally[ename]
            a = sorted(tally.items(), key = lambda (k, v): k)
            x = [k for (k,v) in a]
            y = [v for (k,v) in a]
            #plt.plot(x, y, linewidth=1.0)
            plt.plot(x, y, color=color_dict[ename], linestyle='-', linewidth = 1.0, marker='o', markerfacecolor=color_dict[ename], markersize=6)
            #plt.plot(x, y, color=color_dict[ename], linestyle='-')
        plt.xlabel(x_label)
        plt.ylabel(y_label)
        plt.title(title)
        plt.show()
        
    def tally_times(self, times, granularity = "hr"):
        if granularity == "hr":
            dts = [datetime.fromtimestamp(t).hour for t in times]
        elif granularity == "days":
            dts = [date.fromtimestamp(t).day + date.fromtimestamp(t).month * 30 for t in times]
            min_dts = min(dts)
            dts = [d - min_dts for d in dts]
        elif granularity == "weekday":
            dts = [date.fromtimestamp(t).weekday() for t in times]
        else:
            raise Exception("invalid granularity")
        d = {}
        for t in dts:
            d[t] = d.get(t, 0) + 1
        return d
    
    def plot_all(self):
        #self.plot_multiple_events([u'start-hunt', u'create_hunt', u'AppOpened', u'end-hunt', u'user-login'], "Activity Over Time")
        
        plt.figure(1)
        color_dict = {u'start-hunt': "red", u'create_hunt': "blue", u'AppOpened': "green", u'end-hunt': "black", u'user-login': "magenta"}
        a = set([])
        for ename in [u'start-hunt', u'create_hunt', u'AppOpened', u'end-hunt', u'user-login']:
            tally = self.event_to_tally[ename]
            [a.add(k) for k in tally.keys()]
        x = np.array(sorted(list(a)))
        ys = []
        for ename in [u'start-hunt', u'create_hunt', u'AppOpened', u'end-hunt', u'user-login']:
            y = []
            for x_val in x:
                y.append((self.event_to_tally[ename]).get(x_val, 0))
            ys.append(np.array(y))

        plt.subplot(231)
        plt.fill(x, ys[0], 'red', alpha=0.3)
        plt.plot(x, ys[0], color='red', linestyle='-', linewidth = 1.0, marker='o', markerfacecolor='red', markersize=4)
        plt.title("Start Hunt Events vs. Time (" + self.granularity + ")" )
        plt.xlabel("Time (" + self.granularity + ")")
        plt.ylabel("Start Hunt Events")
        
        plt.subplot(232)
        plt.fill(x, ys[1], 'blue', alpha=0.3)
        plt.plot(x, ys[1], color='blue', linestyle='-', linewidth = 1.0, marker='o', markerfacecolor='blue', markersize=4)
        plt.title("Create Hunt Events vs. Time (" + self.granularity + ")" )
        plt.xlabel("Time (" + self.granularity + ")")
        plt.ylabel("Create Hunt Events")

        plt.subplot(233)
        plt.fill(x, ys[3], 'green', alpha=0.3)
        plt.plot(x, ys[3], color='green', linestyle='-', linewidth = 1.0, marker='o', markerfacecolor='green', markersize=4)
        plt.title("End Hunt Events vs. Time (" + self.granularity + ")" )
        plt.xlabel("Time (" + self.granularity + ")")
        plt.ylabel("End Hunt Events")

        plt.subplot(234)
        plt.fill(x, ys[2], 'black', alpha=0.3)
        plt.plot(x, ys[2], color='black', linestyle='-', linewidth = 1.0, marker='o', markerfacecolor='black', markersize=4)
        plt.title("App-Opened Events vs. Time (" + self.granularity + ")" )
        plt.xlabel("Time (" + self.granularity + ")")
        plt.ylabel("App-Opened Events")

        plt.subplot(235)
        plt.fill(x, ys[4], 'magenta', alpha=0.3)
        plt.plot(x, ys[4], color='magenta', linestyle='-', linewidth = 1.0, marker='o', markerfacecolor='magenta', markersize=4)
        plt.title("User Login Events vs. Time (" + self.granularity + ")" )
        plt.xlabel("Time (" + self.granularity + ")")
        plt.ylabel("User Login Events")

        plt.subplot(236)
        e_names = [u'start-hunt', u'create_hunt', u'AppOpened', u'end-hunt', u'user-login']
        title = "Activity vs. Time (" + self.granularity + ")"
        x_label = "Activity"
        y_label = "Time (" + self.granularity + ")"
        color_dict = {u'start-hunt': "red", u'create_hunt': "blue", u'AppOpened': "black", u'end-hunt': "green", u'user-login': "magenta"}
        for ename in e_names:
            tally = self.event_to_tally[ename]
            a = sorted(tally.items(), key = lambda (k, v): k)
            x = [k for (k,v) in a]
            y = [v for (k,v) in a]
            #plt.plot(x, y, linewidth=1.0)
            plt.plot(x, y, color=color_dict[ename], linestyle='-', linewidth = 1.0, marker='o', markerfacecolor=color_dict[ename], markersize=6)
            #plt.plot(x, y, color=color_dict[ename], linestyle='-')
        plt.xlabel(x_label)
        plt.ylabel(y_label)
        plt.title(title)
        
        #plt.fill(x, ys[0], 'red', x, ys[1], 'blue', x, ys[2], 'green', x, ys[3], 'black', x, ys[4], 'magenta', alpha=0.3)
        
        plt.show()

        '''for e_name in [u'start-hunt', u'create_hunt', u'AppOpened', u'end-hunt', u'user-login']:
            title = {u'start-hunt': "Hunts Started", u'create_hunt': "Hunts Created", u'AppOpened': "App-Opens", u'end-hunt': "Hunts Completed", u'user-login': "User Logins "}[e_name]
            title = title + " vs. Time (hrs)"
            self.plot_multiple_events([e_name], title)'''
    
p = ParsePlotter()
p.plot_all()

p = ParsePlotter(granularity = "days")
p.plot_all()

p = ParsePlotter(granularity = "weekday")
p.plot_all()

'''import json
from pprint import pprint
import plotly.plotly as py
from plotly.graph_objs import *
from datetime import date

import numpy as np

class ParsePlotter:
    def __init__(self):
        #event names: u'start-hunt', u'create_hunt', u'AppOpened', u'end-hunt', u'user-login'
        self.event_to_time = {}
        with open("./all_parse_scavengr_data.csv") as data_file:
            for line in data_file:
                data = json.loads(line)
                event_name = data["Event Name"]
                self.event_to_time[event_name] = self.event_to_time.get(event_name, []) + [float(data[u'Timestamp (s)'])]
    #def plot_event(self, e_name):
    #    times = event_to_time[e_name]
    #    title = {u'start-hunt': "Hunts Started", u'create_hunt': "Hunts Created", u'AppOpened': "App-Opens", u'end-hunt': "Hunts Completed", u'user-login': "User Logins "}[e_name]
    def plot_multiple_events(self, e_names, title):
        data = Data([Histogram(x = np.asarray([date.fromtimestamp(t) for t in self.event_to_time[ename]])) for ename in e_names])
        layout = Layout(barmode = "stack")
        fig = Figure(data=data, layout=layout)
        plot_url = py.plot(fig, filename=title)
        
    def plot_all(self):
        for e_name in [u'start-hunt', u'create_hunt', u'AppOpened', u'end-hunt', u'user-login']:
            title = {u'start-hunt': "Hunts Started", u'create_hunt': "Hunts Created", u'AppOpened': "App-Opens", u'end-hunt': "Hunts Completed", u'user-login': "User Logins "}[e_name]
            title = title + " vs. Time (hrs)"
            self.plot_multiple_events([e_name], title)
    
p = ParsePlotter()
p.plot_all()''' #plotly implementation

