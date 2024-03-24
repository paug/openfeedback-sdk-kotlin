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

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}
