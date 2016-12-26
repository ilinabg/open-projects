function varargout = sleep_apnea(varargin)
% SLEEP_APNEA MATLAB code for sleep_apnea.fig
%      SLEEP_APNEA, by itself, creates a new SLEEP_APNEA or raises the existing
%      singleton*.
%
%      H = SLEEP_APNEA returns the handle to a new SLEEP_APNEA or the handle to
%      the existing singleton*.
%
%      SLEEP_APNEA('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in SLEEP_APNEA.M with the given input arguments.
%
%      SLEEP_APNEA('Property','Value',...) creates a new SLEEP_APNEA or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before sleep_apnea_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to sleep_apnea_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help sleep_apnea

% Last Modified by GUIDE v2.5 31-Jul-2013 15:21:04

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @sleep_apnea_OpeningFcn, ...
                   'gui_OutputFcn',  @sleep_apnea_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before sleep_apnea is made visible.
function sleep_apnea_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to sleep_apnea (see VARARGIN)

% Choose default command line output for sleep_apnea
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);
setappdata(0, 'handle', gcf);

% UIWAIT makes sleep_apnea wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = sleep_apnea_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;



% --- Executes on button press in pushbutton1.
function pushbutton1_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)



function edit1_Callback(hObject, eventdata, handles)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit1 as text
%        str2double(get(hObject,'String')) returns contents of edit1 as a double


% --- Executes during object creation, after setting all properties.
function edit1_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in pushbutton2.
function pushbutton2_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on button press in pushbutton3.
function pushbutton3_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
filename = uigetfile({'*.mat'},'Choose date file')
load(filename);
t = dat(1,:);
y = dat(2,:);
fig = plot(t,y,'Parent', handles.axes1);
handle = getappdata(0, 'handle');
setappdata(handle, 'time', t);
setappdata(handle, 'resp', y);

%To reload the global variables wherever you want:
%handle = getappdata(0, 'handle');
%t = getappdata(handle, 'time');




