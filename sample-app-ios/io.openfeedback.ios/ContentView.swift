//
//  ContentView.swift
//  io.openfeedback.ios
//
//  Created by Martin on 24/03/2024.
//

import SwiftUI
import UIKit
import SwiftUI
import SampleApp
import FirebaseCore

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
//        let options = FirebaseOptions(googleAppID: "1:635903227116:web:31de912f8bf29befb1e1c9", gcmSenderID: "lknlkn")
//        FirebaseApp.configure(options: options)
//        
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}
