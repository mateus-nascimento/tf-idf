# -*- coding: utf-8 -*-
"""
Created on Sat May 12 19:47:28 2018

@author: mateus.n.silva
"""
import pandas as pd
from nltk.corpus import stopwords
from string import punctuation
import nltk
import matplotlib.pyplot as plt

generos = ['Country', 'Electronic', 'Folk', 'Hip-Hop', 'Jazz', 'Metal', 'Not Available', 'Other', 'Pop', 'Rock']
stopWords = stopwords.words('english') + list(punctuation)

path = 'C:/Users/mateus.n.silva/Documents/PosGraduacao/05_PLN_Jamilson/projeto/'
arquivo = 'lyrics.csv'
caminho = path+arquivo
df = pd.read_csv(caminho, delimiter=",", usecols=['genre', 'lyrics'])

print("Músicas por gêneros:")
for genero in generos:
    
    file = open('Relatorio_' + genero +'.txt', 'w', encoding='utf-8')
    
    #A musica eh do genero escolhido?
    isGenero = df['genre']==genero
    musicas = list(df[isGenero].lyrics)
    quantidade = len(musicas)
    
    if quantidade > 0:
        print(genero + " - " + str(quantidade))
        
        letras = []
        indice = 0
        for item in musicas:
            indice += 1
            try:
                #tokens = word_tokenize(item)
                tokens = item.split()
        
                for token in tokens:
                    palavraMinuscula = str.lower(token)
                    
                    if palavraMinuscula not in stopWords:
                        letras.append(palavraMinuscula)            
            except:
                pass
               
        #Contando a frequencia das palavras (Count Word Frequency)
        freq = nltk.FreqDist(letras) 
            
        #Grafico com ocorrencias das palavras mais utilizadas
        freq.plot(20, cumulative=False)
    file.close()
    