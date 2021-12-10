FROM python:3

RUN pip install pandas
RUN pip install --upgrade pip
RUN pip install --upgrade tensorflow
WORKDIR /app

COPY . .

RUN pip install -r requirements.txt

CMD python ./src/predictor_api.py
