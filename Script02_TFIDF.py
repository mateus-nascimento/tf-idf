# -*- coding: utf-8 -*-
"""
Created on Sat May 19 09:42:42 2018

@author: mateus.n.silva
"""
import pandas as pd
from collections import defaultdict
import math
from string import punctuation
from nltk.corpus import stopwords
from nltk import word_tokenize

path = 'C:/Users/mateus.n.silva/Documents/PosGraduacao/05_PLN_Jamilson/projeto/'
arquivo = 'lyrics.csv'
caminho = path+arquivo
df = pd.read_csv(caminho, delimiter=",", usecols=['genre', 'lyrics'])

generos = ['Country', 'Electronic', 'Folk', 'Hip-Hop', 'Jazz', 'Metal', 'Not Available', 'Other', 'Pop', 'Rock']

stop_words = stopwords.words('english') + list(punctuation)

def tokenize(text):
    words = word_tokenize(text)
    #words = text.split()
    words = [w.lower() for w in words]
    return [w for w in words if w not in stop_words and not w.isdigit()]


for genero in generos:
    
    fileScore = open('Score_' + genero + '.txt', 'w', encoding='utf-8')
    
    isGenero = df['genre']==genero
    musicas = list(df[isGenero].lyrics)
    quantidade = len(musicas)
    #print(genero + " - " + str(quantidade))
    
    #Parte 1
    vocabulary = set()
    for musica in musicas:
        try:
            words = tokenize(musica)
            vocabulary.update(words)
        except:
            pass
        
    vocabulary = list(vocabulary)
    word_index = {w: idx for idx, w in enumerate(vocabulary)}
    
    VOCABULARY_SIZE = len(vocabulary)
        
    #Quantidade de palavras nos documentos
    #print(VOCABULARY_SIZE, quantidade)
            
    #Parte 2
    word_idf = defaultdict(lambda: 0)
    for musica in musicas:
        try:
            words = set(tokenize(musica))
            for word in words:
                word_idf[word] += 1
        except:
            pass
        
    for word in sorted(vocabulary):
        score = math.log(quantidade / float(word_idf[word] + 1))
        fileScore.write(word + ' - ' + str(score) + "\n")
    
fileScore.close()
